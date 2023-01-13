package com.spartan.dc.strategy.impl;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spartan.dc.core.dto.payment.CreateChargeReqDTO;
import com.spartan.dc.core.dto.payment.CreateChargeRespDTO;
import com.spartan.dc.core.dto.payment.PricingEntry;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.UUIDUtil;
import com.spartan.dc.core.util.okhttp.OkhttpRequestUtil;
import com.spartan.dc.dao.write.ChainSalePriceMapper;
import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.dao.write.DcPaymentOrderMapper;
import com.spartan.dc.dao.write.DcPaymentTypeMapper;
import com.spartan.dc.delayed.entity.QueryPayResultEntity;
import com.spartan.dc.handler.PaymentHandler;
import com.spartan.dc.model.*;
import com.spartan.dc.model.vo.req.PaymentReqVO;
import com.spartan.dc.model.vo.req.RefundReqVO;
import com.spartan.dc.model.vo.resp.PaymentRespVO;
import com.spartan.dc.model.vo.resp.RefundRespVO;
import com.spartan.dc.strategy.StrategyService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Stablecoins
 *
 * @Author : rjx
 * @Date : 2022/10/10 17:23
 **/
@Service
@Slf4j
public class CoinbasePayStrategyImpl implements StrategyService {

    @Value("${payment.coinbase.server-addr}")
    private String serverAddr;
    @Value("${payment.coinbase.api.createCharge}")
    private String createCharge;
    @Value("${payment.coinbase.api.queryCharge}")
    private String queryCharge;

    private static OkhttpRequestUtil okhttpRequestUtil = OkhttpRequestUtil.getInstance();
    private static final String PAY_NAME = "Gas charge";
    private static final String PAY_DESCRIPTION = "";
    private static final String PRICING_TYPE = "fixed_price";
    private static final String LOCAL_CURRENCY = "USD";
    private static final String COINBASE_HEADER_API_KEY = "X-CC-Api-Key";
    private static final String COINBASE_HEADER_API_VERSION = "X-CC-Version";
    private static final Integer COINBASE_CREATE_CHARGE_SUCCESS_STATUS = 201;
    private static final Integer COINBASE_AUTH_STATUS = 401;
    public static final String QUERY_PAY_RESULT_KEY = "query_pay_result_";
    public static final Integer QUERY_PAY_RESULT_EXPIRE = 30;


    @Resource
    private PaymentHandler paymentHandler;
    @Resource
    private DcPaymentTypeMapper dcPaymentTypeMapper;
    @Resource
    private ChainSalePriceMapper chainSalePriceMapper;
    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;
    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;


    @Override
    public String fetchKey() {
        return PayChannelTypeEnum.PAY_COINBASE.getCode();
    }

    @Override
    public PaymentRespVO createOrder(PaymentReqVO paymentReqVO, DcChainAccess dcChainAccess) {
        log.info("Create a payment order---Payment method：{}", PayChannelTypeEnum.PAY_COINBASE.getName());

        // Get payment information
        DcPaymentType dcPaymentType = dcPaymentTypeMapper.selectPaymentByCode(paymentReqVO.getChannelCode());
        if (Objects.isNull(dcPaymentType)) {
            throw new GlobalException("Payment information is empty");
        }
        String apiKey = dcPaymentType.getApiKey();
        String apiVersion = dcPaymentType.getApiVersion();

        // Generate order number
        String tradeNo = CodePrefixEnum.SPARTANPAY_ + UUIDUtil.generate();

        // Get the current price
        ChainSalePrice chainSalePrice = chainSalePriceMapper.selectCurrentSalePrice(paymentReqVO.getChainId());
        if (Objects.isNull(chainSalePrice)) {
            throw new GlobalException("No available gas price");
        }

        // Assembly price information
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("customerEmail", paymentReqVO.getEmail());
        metaData.put("tradeNo", tradeNo);

        // Assembly price information
        PricingEntry pricingEntry = PricingEntry.builder()
                .currency(LOCAL_CURRENCY)
                .amount(String.valueOf(NumberUtil.round(paymentReqVO.getPayAmount().doubleValue() / 100, 2)))
                .build();

        // Assemble the parameters required by coinbase to create the order
        CreateChargeReqDTO createChargeReqDTO = CreateChargeReqDTO.builder()
                .name(PAY_NAME)
                .description(PAY_DESCRIPTION)
                .metadata(metaData)
                .pricingType(PRICING_TYPE)
                .localPrice(pricingEntry)
                .cancelUrl("")
                .build();

        // coinbase creates the order url
        String url = serverAddr + createCharge;
        Response response;
        PaymentRespVO paymentRespVO = new PaymentRespVO();
        log.info("Create a payment order---Request url: {}, Parameter of request: {}", url, JSON.toJSONString(createChargeReqDTO));
        // Request coinbase to create an order
        try {
            response = okhttpRequestUtil.postJSON(
                    url,
                    getHeader(apiKey, apiVersion),
                    null,
                    createChargeReqDTO
            );

            if (Objects.isNull(response)) {
                throw new GlobalException("coinbase Coinbase response result is empty");
            }

            log.info("Create a payment order---Results of responsecode: {}", response.code());

            if (Objects.equals(COINBASE_CREATE_CHARGE_SUCCESS_STATUS, response.code())) {
                // Parse the returned data
                JSONObject responseBody = JSONObject.parseObject(response.body().string());
                String data = responseBody.getString("data");
                CreateChargeRespDTO createChargeRespDTO = JSONObject.parseObject(data, CreateChargeRespDTO.class);

                // Assemble payment order information into the warehouse
                DcPaymentOrder dcPaymentOrder = DcPaymentOrder.builder()
                        .accountAddress(paymentReqVO.getChainAccountAddress())
                        .chainId(paymentReqVO.getChainId())
                        .salePriceId(chainSalePrice.getSalePriceId())
                        .paymentTypeId(dcPaymentType.getPaymentTypeId())
                        .tradeNo(tradeNo)
                        .otherTradeNo(createChargeRespDTO.getId())
                        .email(paymentReqVO.getEmail())
                        .currency(LOCAL_CURRENCY)
                        .gasCount(paymentReqVO.getGasCount())
                        .payAmount(NumberUtil.round(paymentReqVO.getPayAmount().doubleValue() / 100, 2))
                        .payState(PayStateEnum.PENDING.getCode())
                        .gasRechargeState(RechargeStateEnum.NO_PROCESSING_REQUIRED.getCode())
                        .updateTime(new Date())
                        .createTime(new Date())
                        .build();
                dcPaymentOrderMapper.insertSelective(dcPaymentOrder);

                // Assemble return parameters
                paymentRespVO.setTradeNo(tradeNo);
                paymentRespVO.setOtherTradeNo(createChargeRespDTO.getId());
                paymentRespVO.setOrderUrl(createChargeRespDTO.getHostedUrl());

                // Send an order success email
                paymentHandler.sendOrderSubmitEmail(dcPaymentOrder, createChargeRespDTO.getHostedUrl());

            } else if (Objects.equals(COINBASE_AUTH_STATUS, response)) {
                throw new GlobalException("Coinbase coinbase permission verification failed");
            } else {
                throw new GlobalException("Failed to request for Coinbase");
            }
        } catch (IOException e) {
            throw new GlobalException(e.getMessage());
        }
        log.info("Create payment order successfully---Return results：{}", JSON.toJSONString(paymentRespVO));

        return paymentRespVO;
    }

    @Override
    public RefundRespVO refund(RefundReqVO refundReqVO, long currencyUserId) {
        RefundRespVO response = new RefundRespVO();
        DcPaymentOrder dcPaymentOrder = dcPaymentOrderMapper.selectOrderByTradeNo(refundReqVO.getTradeNo());
        if (Objects.isNull(dcPaymentOrder)) {
            throw new GlobalException("Order does not exist");
        }

        if (!dcPaymentOrder.getGasRechargeState().equals(RechargeStateEnum.FAILED.getCode())) {
            throw new GlobalException("This order does not support refund operation.");
        }

        if (dcPaymentOrder.getIsRefund().equals(OrderRefundStateEnum.REFUNDED.getCode())) {
            throw new GlobalException(String.format("%s,Already refunded, please do not repeat the operation.", refundReqVO.getTradeNo()));
        }

        // Refund order payment number
        String refundTradeNo = CodePrefixEnum.SPARTANREFUND_ + UUIDUtil.generate();

        // Update bill status
        dcPaymentOrder.setIsRefund(OrderRefundStateEnum.REFUNDED.getCode());
        dcPaymentOrderMapper.updateByPrimaryKey(dcPaymentOrder);

        paymentHandler.insertPaymentRefund(refundReqVO.getReason(), currencyUserId, dcPaymentOrder, refundTradeNo, dcPaymentOrder.getOtherTradeNo());
        response.setStatus(RefundStateEnum.SUCCESS.getCode());
        return response;
    }

    @Override
    public boolean queryPayResult(QueryPayResultEntity queryPayResultEntity) {
        log.info("Payment result query---order【{}】Query results start，Method of payment: {}", queryPayResultEntity.getTradeNo(), PayChannelTypeEnum.PAY_COINBASE.getName());

        // Check if the order exists
        DcPaymentOrder selectOrderByTradeNo = dcPaymentOrderMapper.selectOrderByTradeNo(queryPayResultEntity.getTradeNo());
        if (Objects.isNull(selectOrderByTradeNo)) {
            log.info("Payment result query---Order does not exist");
            return true;
        }

        // Determine whether the payment is successful
        if (Objects.equals(PayStateEnum.SUCCESS.getCode(), selectOrderByTradeNo.getPayState())) {
            log.info("Payment result query---The order has been paid successfully");
            return true;
        }

        // Get payment information
        DcPaymentType dcPaymentType = dcPaymentTypeMapper.selectPaymentByCode(queryPayResultEntity.getChannelCode());
        if (Objects.isNull(dcPaymentType)) {
            throw new GlobalException("Payment information is empty");
        }
        String apiKey = dcPaymentType.getApiKey();
        String apiVersion = dcPaymentType.getApiVersion();

        // The payment result query url concatenates the third-party pipeline number
        String url = serverAddr + queryCharge + queryPayResultEntity.getOtherTradeNo();
        log.info("Payment result query---request url：{}", url);

        // Request coinbase payment result query
        try {
            Response response = okhttpRequestUtil.get(url, getHeader(apiKey, apiVersion), null);

            if (Objects.isNull(response)) {
                throw new GlobalException("coinbase Coinbase response result is empty");
            }

            log.info("Payment result query -- Response result code:{}", JSON.toJSONString(response.code()));
            // Response result processing
            if (Objects.equals(HttpStatus.OK.value(), response.code())) {
                // Parse the returned data
                JSONObject responseBody = JSONObject.parseObject(response.body().string());
                String data = responseBody.getString("data");
                log.debug("Payment result query - parsed data：{}", data);
                CreateChargeRespDTO createChargeRespDTO = JSONObject.parseObject(data, CreateChargeRespDTO.class);

                // Determine whether the order is expired, if the invalidation status is changed to payment failure
                long currentTime = System.currentTimeMillis();
                if (currentTime > createChargeRespDTO.getExpiresAt().getTime()) {
                    log.info("Payment result Query -- The order is invalid");
                    paymentHandler.updateOrderToFail(queryPayResultEntity.getTradeNo());
                    return true;
                }

                // Order payment information, if empty, will be returned directly
                if (CollectionUtils.isEmpty(createChargeRespDTO.getPayments())) {
                    log.info("Payment Result Query -- There is no payment information for this order");
                    return false;
                }

                // Payment result processing
                Boolean dealPayResult = dealPayResult(createChargeRespDTO, selectOrderByTradeNo);
                if (dealPayResult) {
                    return true;
                }

            } else if (Objects.equals(COINBASE_AUTH_STATUS, response.code())) {
                log.error("Coinbase coinbase permission verification failed");
                return false;
            } else {
                log.error("Failed to request for Coinbase");
                return false;
            }
        } catch (IOException e) {
            log.error("Payment result query -- Abnormal：{}", e);
            return false;
        }

        return false;
    }

    @Override
    public boolean refundResult(QueryPayResultEntity queryPayResultEntity) {
        return false;
    }

    @Override
    public boolean stripeCallback(String json, String reqSignature) {
        return false;
    }

    @Override
    public boolean coinbaseCallback(String json) {
        CreateChargeRespDTO createChargeRespDTO = JSONObject.parseObject(json, CreateChargeRespDTO.class);
        Map<String, Object> metadata = createChargeRespDTO.getMetadata();
        String tradeNo = metadata.get("tradeNo").toString();

        DcPaymentOrder selectOrderByTradeNo = dcPaymentOrderMapper.selectOrderByTradeNo(tradeNo);
        if (Objects.isNull(selectOrderByTradeNo)) {
            return false;
        }

        if (Objects.equals(PayStateEnum.SUCCESS.getCode(), selectOrderByTradeNo.getPayState())) {
            return false;
        }

        return dealPayResult(createChargeRespDTO, selectOrderByTradeNo);
    }

    /**
     * Payment result processing
     */
    @Transactional
    public Boolean dealPayResult(CreateChargeRespDTO createChargeRespDTO, DcPaymentOrder dcPaymentOrder) {
        CreateChargeRespDTO.Payment payment = createChargeRespDTO.getPayments().get(0);
        // If the payment status is CONFIRMED, modify the local status to payment success
        if (Objects.equals("CONFIRMED", payment.getStatus())) {
            BigDecimal amount = new BigDecimal(payment.getValue().getCrypto().getAmount());
            String currency = payment.getValue().getCrypto().getCurrency();

            // Get the exchange rate
            StringBuilder exRates = new StringBuilder();
            Map<String, String> exchangeRates = createChargeRespDTO.getExchangeRates();
            exchangeRates.forEach((k, v) -> {
                if (k.contains(currency)) {
                    exRates.append(v);
                }
            });

            // Assembly order information
            DcPaymentOrder paymentOrder = DcPaymentOrder.builder()
                    .orderId(dcPaymentOrder.getOrderId())
                    .payAmount(amount)
                    .currency(currency)
                    .payState(PayStateEnum.SUCCESS.getCode())
                    .payTime(createChargeRespDTO.getConfirmedAt())
                    .txHash(payment.getTransactionId())
                    .txTime(createChargeRespDTO.getConfirmedAt())
                    .exRates(exRates.toString())
                    .gasRechargeState(RechargeStateEnum.PENDING.getCode())
                    .build();
            dcPaymentOrderMapper.updateByPrimaryKeySelective(paymentOrder);

            // Check whether the gas recharge record has been added according to orderId. No new gas recharge record has been added, otherwise skip
            DcGasRechargeRecord rechargeRecord = dcGasRechargeRecordMapper.selectByOrderId(dcPaymentOrder.getOrderId());
            if (Objects.isNull(rechargeRecord)) {
                // Assemble the gas recharge record, and the storage state is to be processed, waiting for the timing task recharge
                DcGasRechargeRecord dcGasRechargeRecord = DcGasRechargeRecord.builder()
                        .chainId(dcPaymentOrder.getChainId())
                        .orderId(dcPaymentOrder.getOrderId())
                        .chainAddress(dcPaymentOrder.getAccountAddress())
                        .gas(dcPaymentOrder.getGasCount())
                        .state(RechargeSubmitStateEnum.PENDING_SUBMIT.getCode())
                        .updateTime(new Date())
                        .rechargeState(RechargeStateEnum.NO_PROCESSING_REQUIRED.getCode())
                        .build();
                dcGasRechargeRecordMapper.insertRechargeRecord(dcGasRechargeRecord);
            } else {
                log.info("Payment result Query --gas recharge record already exists");
            }

            // Send a payment success email
            dcPaymentOrder.setPayAmount(amount);
            dcPaymentOrder.setExRates(exRates.toString());
            dcPaymentOrder.setPayTime(createChargeRespDTO.getConfirmedAt());
            dcPaymentOrder.setTxHash(payment.getTransactionId());
            dcPaymentOrder.setTxTime(createChargeRespDTO.getTimeline().get(0).getTime());
            Map<String, Object> metadata = createChargeRespDTO.getMetadata();
            String customerEmail = metadata.get("customerEmail").toString();
            paymentHandler.sendPaySuccessEmail(dcPaymentOrder, customerEmail);

            log.info("Payment result query --NTT recharge record was successfully stored");
            return true;
        }

        return false;
    }


    private Map<String, String> getHeader(String apiKey, String apiVersion) {
        Map<String, String> header = new HashMap<>();
        header.put("accept", MediaType.APPLICATION_JSON_VALUE);
        header.put("content-type", MediaType.APPLICATION_JSON_VALUE);
        header.put(COINBASE_HEADER_API_KEY, apiKey);
        header.put(COINBASE_HEADER_API_VERSION, apiVersion);
        return header;
    }

}
