package com.spartan.dc.strategy.impl;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.UUIDUtil;
import com.spartan.dc.dao.write.*;
import com.spartan.dc.delayed.entity.QueryPayResultEntity;
import com.spartan.dc.handler.PaymentHandler;
import com.spartan.dc.model.*;
import com.spartan.dc.model.vo.req.PaymentReqVO;
import com.spartan.dc.model.vo.req.RefundReqVO;
import com.spartan.dc.model.vo.resp.PaymentRespVO;
import com.spartan.dc.model.vo.resp.RefundRespVO;
import com.spartan.dc.service.SendMessageService;
import com.spartan.dc.strategy.StrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Offline remittance
 *
 * @Author : rjx
 * @Date : 2022/11/04 17:23
 **/
@Service
@Slf4j
public class OfflinePayStrategyImpl implements StrategyService {

    @Resource
    private PaymentHandler paymentHandler;
    @Resource
    private DcPaymentTypeMapper dcPaymentTypeMapper;
    @Resource
    private ChainSalePriceMapper chainSalePriceMapper;
    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;
    @Resource
    private SendMessageService sendMessageService;

    private final static String CURRENCY = "USD";

    @Override
    public String fetchKey() {
        return PayChannelTypeEnum.PAY_OFFLINE.getCode();
    }

    @Override
    @Transactional
    public PaymentRespVO createOrder(PaymentReqVO paymentReqVO, DcChainAccess dcChainAccess) {
        log.info("Create a payment order---Payment method：{}", PayChannelTypeEnum.PAY_OFFLINE.getName());

        // Get payment information
        DcPaymentType dcPaymentType = dcPaymentTypeMapper.selectPaymentByCode(paymentReqVO.getChannelCode());
        if (Objects.isNull(dcPaymentType)) {
            throw new GlobalException("Payment information is empty");
        }

        // Generate order number
        String tradeNo = CodePrefixEnum.SPARTANPAY_ + UUIDUtil.generate();

        // Get the current price
        ChainSalePrice chainSalePrice = chainSalePriceMapper.selectCurrentSalePrice(paymentReqVO.getChainId());
        if (Objects.isNull(chainSalePrice)) {
            throw new GlobalException("No available gas price");
        }

        // Assemble payment order information
        DcPaymentOrder dcPaymentOrder = DcPaymentOrder.builder()
                .accountAddress(paymentReqVO.getChainAccountAddress())
                .chainId(paymentReqVO.getChainId())
                .salePriceId(chainSalePrice.getSalePriceId())
                .paymentTypeId(dcPaymentType.getPaymentTypeId())
                .tradeNo(tradeNo)
                .email(paymentReqVO.getEmail())
                .currency(CURRENCY)
                .gasCount(paymentReqVO.getGasCount())
                .payAmount(NumberUtil.round(paymentReqVO.getPayAmount().doubleValue() / 100, 2))
                .payState(PayStateEnum.PENDING.getCode())
                .gasRechargeState(RechargeStateEnum.NO_PROCESSING_REQUIRED.getCode())
                .updateTime(new Date())
                .createTime(new Date())
                .build();
        dcPaymentOrderMapper.insertSelective(dcPaymentOrder);

        // Send order information to customer
        sendOrderSubmitEmail(dcPaymentOrder, dcPaymentType);

        PaymentRespVO paymentRespVO = PaymentRespVO.builder()
                .tradeNo(dcPaymentOrder.getTradeNo())
                .build();

        log.info("Create payment order successfully --- return result：{}", JSON.toJSONString(paymentRespVO));
        return paymentRespVO;
    }


    @Override
    @Transactional
    public RefundRespVO refund(RefundReqVO refundReqVO, long currentUserId) {
        RefundRespVO response = new RefundRespVO();
        DcPaymentOrder dcPaymentOrder = dcPaymentOrderMapper.selectOrderByTradeNo(refundReqVO.getTradeNo());
        if (Objects.isNull(dcPaymentOrder)) {
            throw new GlobalException(" Order does not exist");
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

        paymentHandler.insertPaymentRefund(refundReqVO.getReason(), currentUserId, dcPaymentOrder, refundTradeNo, dcPaymentOrder.getOtherTradeNo());
        response.setStatus(RefundStateEnum.SUCCESS.getCode());
        return response;
    }

    @Override
    public boolean queryPayResult(QueryPayResultEntity queryPayResultEntity) {
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
        return false;
    }


    /**
     * Send the offline order to the successful email
     */
    private void sendOrderSubmitEmail(DcPaymentOrder dcPaymentOrder, DcPaymentType dcPaymentType) {
        List<String> receivers = Lists.newArrayList();
        receivers.add(dcPaymentOrder.getEmail());

        // Parameter of assembly
        Map<String, Object> replaceContentMap = new HashMap<>();
        replaceContentMap.put("trade_no_", dcPaymentOrder.getTradeNo());
        replaceContentMap.put("account_address_", dcPaymentOrder.getAccountAddress());
        replaceContentMap.put("gas_count_", dcPaymentOrder.getGasCount().longValue());
        replaceContentMap.put("pay_amount_", String.format("%.2f", dcPaymentOrder.getPayAmount()));
        replaceContentMap.put("bank_name_", dcPaymentType.getBankName());
        replaceContentMap.put("bank_account_no_", dcPaymentType.getBankAccount());
        replaceContentMap.put("bank_address_", dcPaymentType.getBankAddress());
        replaceContentMap.put("swift_code_", dcPaymentType.getSwiftCode());
        replaceContentMap.put("payment_details_", dcPaymentOrder.getTradeNo());

        SendMessageReqVO sendMessageReqVO = new SendMessageReqVO();
        sendMessageReqVO.setMsgCode(MsgCodeEnum.ORDER_SUBMITTED_OFFLINE.getCode());
        sendMessageReqVO.setReceivers(receivers);
        sendMessageReqVO.setReplaceContentMap(replaceContentMap);

        // Send an email
        sendMessageService.sendMessage(sendMessageReqVO);
    }

}
