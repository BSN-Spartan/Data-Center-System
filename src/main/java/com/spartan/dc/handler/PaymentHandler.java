package com.spartan.dc.handler;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
import com.spartan.dc.core.enums.MsgCodeEnum;
import com.spartan.dc.core.enums.PayStateEnum;
import com.spartan.dc.core.enums.RefundStateEnum;
import com.spartan.dc.core.util.common.DateUtils;
import com.spartan.dc.dao.write.DcPaymentOrderMapper;
import com.spartan.dc.dao.write.DcPaymentRefundMapper;
import com.spartan.dc.model.DcPaymentOrder;
import com.spartan.dc.model.DcPaymentRefund;
import com.spartan.dc.service.SendMessageService;
import com.stripe.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author : rjx
 * @Date : 2022/10/19 17:31
 **/
@Component
@Slf4j
public class PaymentHandler {

    @Resource
    private SendMessageService sendMessageService;
    @Resource
    private DcPaymentRefundMapper dcPaymentRefundMapper;
    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;

    /**
     * * Update MemberPaymentOrder status to failed
     */
    public boolean updateOrderToFail(String tradeNo) {
        // Check if the order exists
        DcPaymentOrder selectOrderByTradeNo = dcPaymentOrderMapper.selectOrderByTradeNo(tradeNo);
        if (Objects.isNull(selectOrderByTradeNo)) {
            log.error("Payment result query --- Order does not exist", tradeNo);
            return false;
        }

        // Assemble order information, update status
        DcPaymentOrder paymentOrder = DcPaymentOrder.builder()
                .orderId(selectOrderByTradeNo.getOrderId())
                .payState(PayStateEnum.FAILURE.getCode())
                .updateTime(new Date())
                .build();
        dcPaymentOrderMapper.updateByPrimaryKeySelective(paymentOrder);
        return true;
    }

    /**
     * Send email
     **/
    private void sendEmail(String msgCode, Map<String, Object> replaceContentMap, List<String> receivers) {

        SendMessageReqVO sendMessageReqVO = new SendMessageReqVO();
        sendMessageReqVO.setMsgCode(msgCode);
        sendMessageReqVO.setReplaceContentMap(replaceContentMap);
        sendMessageReqVO.setReceivers(receivers);
        log.info("Sending email general ...... Send content ...... {}]", JSONObject.toJSONString(sendMessageReqVO));

        // Send email
        sendMessageService.sendMessage(sendMessageReqVO);

    }

    /**
      * Send payment success email
      *
      **/
    public void sendOrderSubmitEmail(DcPaymentOrder dcPaymentOrder, String payLink) {
        log.info("Send payment success email......");

        // Assemble mail parameters
        Map<String, Object> replaceContentMap = new HashMap<>();
        replaceContentMap.put("trade_no_", dcPaymentOrder.getTradeNo());
        replaceContentMap.put("account_address_", dcPaymentOrder.getAccountAddress());
        replaceContentMap.put("gas_count_", dcPaymentOrder.getGasCount().longValue());
        replaceContentMap.put("pay_amount_", NumberUtil.round(dcPaymentOrder.getPayAmount(), 2));
        replaceContentMap.put("pay_link_", payLink);

        // Recipient
        List<String> receivers = Lists.newArrayList();
        receivers.add(dcPaymentOrder.getEmail());

        // Send email
        sendEmail(MsgCodeEnum.ORDER_SUBMIT_SUCCESS.getCode(), replaceContentMap, receivers);
    }

    /**
     * Send payment success email
     *
     **/
    public void sendPaySuccessEmail(DcPaymentOrder dcPaymentOrder, String customerEmail) {
        log.info("Send payment success email......");

        BigDecimal payAmount;
        if (StringUtils.isNotBlank(dcPaymentOrder.getExRates())) {
            payAmount = NumberUtil.round(dcPaymentOrder.getPayAmount().multiply(new BigDecimal(dcPaymentOrder.getExRates())), 2);
        } else {
            payAmount = dcPaymentOrder.getPayAmount();
        }

        // Assemble mail parameters
        Map<String, Object> replaceContentMap = new HashMap<>();
        replaceContentMap.put("trade_no_", dcPaymentOrder.getTradeNo());
        replaceContentMap.put("account_address_", dcPaymentOrder.getAccountAddress());
        replaceContentMap.put("gas_count_", dcPaymentOrder.getGasCount().longValue());
        replaceContentMap.put("pay_amount_", String.format("%.2f", payAmount));
        replaceContentMap.put("pay_time_", DateUtils.getMonthYearDate(dcPaymentOrder.getPayTime()));
        if (StringUtils.isNotBlank(dcPaymentOrder.getTxHash())) {
            String txHashContent = "Transaction Hash: " + dcPaymentOrder.getTxHash();
            replaceContentMap.put("tx_hash_", txHashContent);
        } else {
            replaceContentMap.put("tx_hash_", "");
        }

        // Recipient
        List<String> receivers = Lists.newArrayList();
        receivers.add(customerEmail);

        // Send email
        sendEmail(MsgCodeEnum.PAYMENT_SUCCESS.getCode(), replaceContentMap, receivers);

    }

    /**
     * New refund record
     *
     */
    public void insertPaymentRefund(String reason, long operator, DcPaymentOrder dcPaymentOrder, String refundTradeNo, String otherTradeNo) {
        DcPaymentRefund dcPaymentRefund = DcPaymentRefund.builder()
                .refundState(RefundStateEnum.SUCCESS.getCode())
                .orderId(dcPaymentOrder.getOrderId())
                .refundTime(new Date())
                .refundAmount(dcPaymentOrder.getPayAmount())
                .accountAddress(dcPaymentOrder.getAccountAddress())
                .createTime(new Date())
                .remarks(reason)
                .tradeNo(refundTradeNo)
                .accountAddress(dcPaymentOrder.getAccountAddress())
                .otherTradeNo(otherTradeNo)
                .operator(operator)
                .updateTime(new Date())
                .build();
        dcPaymentRefundMapper.insert(dcPaymentRefund);
    }

}
