package com.spartan.dc.service.impl;

import com.google.common.collect.Lists;
import com.reddate.spartan.SpartanSdkClient;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.bech32.Bech32Utils;
import com.spartan.dc.core.util.common.AddressUtils;
import com.spartan.dc.core.util.message.ConstantsUtil;
import com.spartan.dc.dao.write.*;
import com.spartan.dc.delayed.DelayQueueHelper;
import com.spartan.dc.delayed.entity.QueryPayResultEntity;
import com.spartan.dc.delayed.entity.RefundResultEntity;
import com.spartan.dc.delayed.producer.QueryPayResultProducer;
import com.spartan.dc.delayed.producer.RefundResultProducer;
import com.spartan.dc.model.*;
import com.spartan.dc.model.vo.req.*;
import com.spartan.dc.model.vo.resp.*;
import com.spartan.dc.service.PaymentService;
import com.spartan.dc.strategy.StrategyHandler;
import com.spartan.dc.strategy.StrategyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.web3j.crypto.WalletUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author : rjx
 * @Date : 2022/10/10 17:26
 **/
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private DcPaymentTypeMapper dcPaymentTypeMapper;
    @Resource
    private ChainSalePriceMapper chainSalePriceMapper;
    @Resource
    private DcChainAccessMapper dcChainAccessMapper;
    @Resource
    private StrategyHandler strategyHandler;
    @Resource
    private QueryPayResultProducer queryPayResultProducer;
    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;
    @Resource
    private DcSystemConfMapper dcSystemConfMapper;
    @Resource
    private RefundResultProducer refundResultProducer;
    @Resource
    private SpartanSdkClient spartanSdkClient;
    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    private DelayQueueHelper<QueryPayResultEntity> delayQueueHelper = DelayQueueHelper.getInstance();

    private static final String COSMOS_ADDRESS_PREFIX = "iaa";


    @Override
    public PaymentRespVO createOrder(PaymentReqVO paymentReqVO, HttpServletRequest request) {

        // Verify whether the verification code is correct
        String captchaSession = request.getSession().getAttribute(ConstantsUtil.GAS_RECHARGE_CAPTCHA_).toString();
        if (!paymentReqVO.getCaptchaCode().equalsIgnoreCase(captchaSession)) {
            throw new GlobalException("Verification code is incorrect");
        }

        String chargeChainAccountAddress = paymentReqVO.getChainAccountAddress();

        if (Objects.equals(ChainTypeEnum.COSMOS.getCode().longValue(), paymentReqVO.getChainId())) {
            if (paymentReqVO.getChainAccountAddress().startsWith("0x")) {
                // Ox transition iaa
                chargeChainAccountAddress = Bech32Utils.hexToBech32(COSMOS_ADDRESS_PREFIX, paymentReqVO.getChainAccountAddress().substring(2));
            }
        }

        // check address
        if (paymentReqVO.getChainId().equals(ChainTypeEnum.COSMOS.getCode().longValue()) && chargeChainAccountAddress.startsWith(COSMOS_ADDRESS_PREFIX)) {
            AddressUtils.validAddress(chargeChainAccountAddress);
        } else {
            if (!WalletUtils.isValidAddress(chargeChainAccountAddress)) {
                throw new GlobalException("Address is illegal");
            }
        }

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            throw new GlobalException("the basic information of the data center has not been configured yet");
        }
        try {
            String accOwner = spartanSdkClient.gasCreditRechargeService.getAccOwner(chargeChainAccountAddress, BigInteger.valueOf(paymentReqVO.getChainId()));
            if (StringUtils.isNotBlank(accOwner) && !sysDataCenter.getDcCode().equalsIgnoreCase(accOwner)) {
                throw new GlobalException("The wallet address has been bound by another data center");
            }
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }

        DcChainAccess dcChainAccess = dcChainAccessMapper.selectByEmail(paymentReqVO.getEmail());
        if (Objects.isNull(dcChainAccess)) {
            String accessKey = UUID.randomUUID().toString().replaceAll("-", "");

            // Gets the data center configuration information table
            String tpdValue = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.CHAIN_INFORMATION_ACCESS.getCode(), SystemConfCodeEnum.TPD.getName());
            String tpsValue = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.CHAIN_INFORMATION_ACCESS.getCode(), SystemConfCodeEnum.TPS.getName());

            // Save user access information
            dcChainAccess = DcChainAccess.builder()
                    .accessKey(accessKey)
                    .contactsEmail(paymentReqVO.getEmail())
                    .state(DcChainAccessStateEnum.START_USING.getCode())
                    .notifyState(DcChainAccessNotifyStateEnum.NOTIFY_FAILURE.getCode())
                    .tps(Integer.valueOf(tpsValue))
                    .tpd(Integer.valueOf(tpdValue))
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            dcChainAccessMapper.insertSelective(dcChainAccess);
        }

        // Calculate the payment amount according to the number of gas
        CalcGasPriceReqVO calcGasPriceReqVO = new CalcGasPriceReqVO();
        calcGasPriceReqVO.setGasCount(paymentReqVO.getGasCount());
        calcGasPriceReqVO.setChainId(paymentReqVO.getChainId());
        calcGasPriceReqVO.setChainAccountAddress(paymentReqVO.getChainAccountAddress());
        CalcGasPriceRespVO calcGasPriceRespVO = calcGasPrice(calcGasPriceReqVO);
        paymentReqVO.setPayAmount(calcGasPriceRespVO.getPayAmount());

        // Select the corresponding payment method according to the payment type
        StrategyService strategy = strategyHandler.getStrategy(paymentReqVO.getChannelCode());
        PaymentRespVO paymentRespVO = strategy.createOrder(paymentReqVO, dcChainAccess);

        // Assemble query payment result producer entity information
        if (!PayChannelTypeEnum.PAY_OFFLINE.getCode().equals(paymentReqVO.getChannelCode())) {
            QueryPayResultEntity queryPayResultEntity = QueryPayResultEntity.builder()
                    .payType(paymentReqVO.getPayType())
                    .channelCode(paymentReqVO.getChannelCode())
                    .tradeNo(paymentRespVO.getTradeNo())
                    .otherTradeNo(paymentRespVO.getOtherTradeNo())
                    .number(1)
                    .build();

            // Call the query payment result producer
            queryPayResultProducer.execute(queryPayResultEntity);
        }

        log.info("End of creating the payment order");
        return paymentRespVO;
    }

    @Override
    public List<PaymentTypeRespVO> paymentType() {
        List<DcPaymentType> dcPaymentTypes = dcPaymentTypeMapper.selectAllPaymentType();
        List<PaymentTypeRespVO> paymentTypeRespVOS = Lists.newArrayList();
        dcPaymentTypes.forEach(sysPaymentType -> {
            PaymentTypeRespVO paymentTypeRespVO = new PaymentTypeRespVO();
            paymentTypeRespVO.setPaymentTypeId(sysPaymentType.getPaymentTypeId());
            paymentTypeRespVO.setPayType(sysPaymentType.getPayType());
            paymentTypeRespVO.setPayChannelName(sysPaymentType.getPayChannelName());
            paymentTypeRespVO.setChannelCode(sysPaymentType.getChannelCode());
            paymentTypeRespVOS.add(paymentTypeRespVO);
        });
        return paymentTypeRespVOS;
    }

    @Override
    public CalcGasPriceRespVO calcGasPrice(CalcGasPriceReqVO calcGasPriceReqVO) {
        // Get the current price
        ChainSalePrice chainSalePrice = chainSalePriceMapper.selectCurrentSalePrice(calcGasPriceReqVO.getChainId());
        if (Objects.isNull(chainSalePrice)) {
            throw new GlobalException("No available gas price");
        }

        BigDecimal gasCount = calcGasPriceReqVO.getGasCount();

        // Gas quantity limit
        BigDecimal minGas = chainSalePrice.getGas().divide(chainSalePrice.getSalePrice()).multiply(new BigDecimal("100"));
        BigDecimal maxGas = chainSalePrice.getGas().divide(chainSalePrice.getSalePrice()).multiply(new BigDecimal("99999999"));
        if (gasCount.compareTo(minGas) < 0 || gasCount.compareTo(maxGas) > 0) {
            throw new GlobalException("The amount of gas cannot be less than" + minGas + "and cannot be greater than" + maxGas);
        }

        // Calculate gas price
        BigDecimal gasPrice = gasCount.divide(chainSalePrice.getGas().divide(chainSalePrice.getSalePrice()), 0);

        CalcGasPriceRespVO calcGasPriceRespVO = CalcGasPriceRespVO.builder()
                .chainAccountAddress(calcGasPriceReqVO.getChainAccountAddress())
                .gasCount(calcGasPriceReqVO.getGasCount())
                .payAmount(gasPrice.longValue())
                .build();

        return calcGasPriceRespVO;
    }

    @Override
    public GasExchangeRateRespVO gasExchangeRate(GasExchangeRateReqVO gasExchangeRateReqVO) {
        // Get the current price
        ChainSalePrice chainSalePrice = chainSalePriceMapper.selectCurrentSalePrice(gasExchangeRateReqVO.getChainId());
        if (Objects.isNull(chainSalePrice)) {
            throw new GlobalException("No available gas price");
        }

        return GasExchangeRateRespVO.builder()
                .gasCount(chainSalePrice.getGas().toString())
                .usPrice(chainSalePrice.getSalePrice().toString())
                .build();
    }

    @Override
    public RefundRespVO refund(RefundReqVO refundReqVO, long currentUserId) {
        StrategyService strategy = strategyHandler.getStrategy(refundReqVO.getChannelCode());
        RefundRespVO refund = strategy.refund(refundReqVO, currentUserId);

        if (RefundStateEnum.SUCCESS.getCode().equals(refund.getStatus())) {
            RefundResultEntity resultEntity = RefundResultEntity.builder()
                    .tradeNo(refund.getTradeNo())
                    .otherTradeNo(refund.getOtherTradeNo())
                    .number(1)
                    .build();
            refundResultProducer.execute(resultEntity);
        }

        return refund;
    }

    @Override
    public boolean queryPayResult(QueryPayResultReqVO queryPayResultReqVO) {
        // Check the order
        DcPaymentOrder dcPaymentOrder = dcPaymentOrderMapper.selectByPrimaryKey(queryPayResultReqVO.getOrderId());
        if (Objects.nonNull(dcPaymentOrder)) {
            DcPaymentType dcPaymentType = dcPaymentTypeMapper.selectByPrimaryKey(dcPaymentOrder.getPaymentTypeId());
            if (Objects.isNull(dcPaymentType)) {
                throw new GlobalException("Payment through unconfigured");
            }

            if (!PayChannelTypeEnum.PAY_OFFLINE.getCode().equals(dcPaymentType.getChannelCode())) {
                // Query the payment result according to the payment channel
                StrategyService strategy = strategyHandler.getStrategy(dcPaymentType.getChannelCode());
                QueryPayResultEntity queryPayResultEntity = QueryPayResultEntity.builder()
                        .payType(dcPaymentType.getPayType())
                        .channelCode(dcPaymentType.getChannelCode())
                        .tradeNo(dcPaymentOrder.getTradeNo())
                        .otherTradeNo(dcPaymentOrder.getOtherTradeNo())
                        .build();
                return strategy.queryPayResult(queryPayResultEntity);
            }
        }
        return false;
    }

    @Override
    public boolean stripeCallback(String json, String reqSignature) {
        StrategyService strategy = strategyHandler.getStrategy(PayChannelTypeEnum.PAY_STRIPE.getCode());
        try {
            return strategy.stripeCallback(json, reqSignature);
        } catch (Exception e) {
            return false;
        }
    }
}
