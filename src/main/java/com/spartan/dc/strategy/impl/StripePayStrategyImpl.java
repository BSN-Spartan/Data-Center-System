package com.spartan.dc.strategy.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
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
import com.spartan.dc.strategy.StrategyService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.InvoiceItemCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static java.awt.SystemColor.info;


/**
 * Fiat currency
 *
 * @Author : rjx
 * @Date : 2022/10/10 17:21
 **/
@Service
@Slf4j
public class StripePayStrategyImpl implements StrategyService {

    @Value("${stripe.currency}")
    private String currency;

    @Value("${stripe.cueDate}")
    private int cueDate;

    private static String DEFAULT_INVOICE_STATUS = "draft";

    private static int DRAFT_MODIFIED_COUNT = 5;

    @Resource
    private PaymentHandler paymentHandler;
    @Resource
    private DcPaymentTypeMapper dcPaymentTypeMapper;
    @Resource
    private ChainSalePriceMapper chainSalePriceMapper;
    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;
    @Resource
    private DcChainAccessMapper dcChainAccessMapper;
    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;
    @Resource
    private DcPaymentRefundMapper dcPaymentRefundMapper;

    @Override
    public String fetchKey() {
        return PayChannelTypeEnum.PAY_STRIPE.getCode();
    }

    /**
     * 1.Check the stripe--customer information associated with the data center side account
     * If it does, it uses customerID. If it does not, it creates customer parameters (email, memberID, name) and binds the data center party
     * 2.Create an Invoice using customerID
     * 3.Return to Payment link
     */
    @Override
    public PaymentRespVO createOrder(PaymentReqVO paymentReqVO, DcChainAccess dcChainAccess) {
        log.info("Create payment order -- payment method：{}", PayChannelTypeEnum.PAY_STRIPE.getName());

        if (paymentReqVO.getPayAmount().compareTo(new BigDecimal("99999999")) > 0) {
            throw new GlobalException("Exceeded the payment limit ($999,999.99)");
        }

        // Get payment information
        DcPaymentType dcPaymentType = dcPaymentTypeMapper.selectPaymentByCode(paymentReqVO.getChannelCode());
        if (Objects.isNull(dcPaymentType)) {
            throw new GlobalException("Payment information is empty");
        }
        String stripePrivateKey = dcPaymentType.getPrivateKey();

        // Generate order number
        String tradeNo = CodePrefixEnum.SPARTANPAY_ + UUIDUtil.generate();

        // Get the current price
        ChainSalePrice chainSalePrice = chainSalePriceMapper.selectCurrentSalePrice(paymentReqVO.getChainId());
        if (Objects.isNull(chainSalePrice)) {
            throw new GlobalException("No available gas price");
        }

        // Check the stripe--customer information associated with the data center side account
        String customerId = createCustomer(stripePrivateKey, paymentReqVO, dcChainAccess);

        // Create a bill
        Invoice invoice = createInvoice(stripePrivateKey, customerId, paymentReqVO, tradeNo);

        // Assemble payment order information into the warehouse
        DcPaymentOrder dcPaymentOrder = createPaymentOrder(paymentReqVO, tradeNo, invoice, chainSalePrice, dcPaymentType);

        // Assemble return parameters
        PaymentRespVO paymentRespVO = new PaymentRespVO();
        paymentRespVO.setTradeNo(tradeNo);
        paymentRespVO.setOtherTradeNo(invoice.getId());
        paymentRespVO.setOrderUrl(invoice.getHostedInvoiceUrl());

        // Send an order success email
        paymentHandler.sendOrderSubmitEmail(dcPaymentOrder, invoice.getHostedInvoiceUrl());

        return paymentRespVO;
    }

    /**
     * stripe Pay a refund
     *
     * @param refundReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundRespVO refund(RefundReqVO refundReqVO, long currentUserId) {
        // Get payment information
        DcPaymentType dcPaymentType = getDcPaymentType(refundReqVO.getChannelCode());

        // return
        RefundRespVO response = new RefundRespVO();
        response.setStatus(RefundStateEnum.ERROR.getCode());
        DcPaymentOrder dcPaymentOrder = dcPaymentOrderMapper.selectOrderByTradeNo(refundReqVO.getTradeNo());
        if (Objects.isNull(dcPaymentOrder)) {
            response.setMessage("Order does not exist");
            return response;
        }

        if (!dcPaymentOrder.getGasRechargeState().equals(RechargeStateEnum.FAILED.getCode())) {
            response.setMessage("This order does not support refund operation.");
            return response;
        }

        if (dcPaymentOrder.getIsRefund().equals(OrderRefundStateEnum.REFUNDED.getCode())) {
            response.setMessage(String.format("%s,Already refunded, please do not repeat the operation.", refundReqVO.getTradeNo()));
            return response;
        }

        try {
            Stripe.apiKey = dcPaymentType.getPrivateKey();
            // Refund order number
            String refundTradeNo = CodePrefixEnum.SPARTANREFUND_ + UUIDUtil.generate();
            Map<String, String> metadata = new HashMap<>(4);
            metadata.put("refundReason", refundReqVO.getReason());
            metadata.put("tradeNo", refundReqVO.getTradeNo());
            metadata.put("refundTradeNo", refundTradeNo);
            metadata.put("operator", String.valueOf(currentUserId));
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(dcPaymentOrder.getPaymentIntent())
                    .setMetadata(metadata)
                    .build();

            Refund refund = Refund.create(params);
            if (Objects.nonNull(refund)) {
                response.setStatus(RefundStateEnum.getEnumByName(refund.getStatus()).getCode());
                response.setOtherTradeNo(refund.getId());
                response.setTradeNo(refundTradeNo);

                // Update bill status
                dcPaymentOrder.setIsRefund(OrderRefundStateEnum.REFUNDED.getCode());
                dcPaymentOrderMapper.updateByPrimaryKey(dcPaymentOrder);

                // New refund record
                insertPaymentRefund(refundReqVO.getReason(), currentUserId, dcPaymentOrder, refundTradeNo, refund);
            } else {
                log.error("stripe Refund failure,Order No:{},Third party serial number:{}", refundReqVO.getTradeNo(), dcPaymentOrder.getOtherTradeNo());
            }
        } catch (StripeException e) {
            log.error("stripe Abnormal refund,Order No:{},Third party serial number:{}", refundReqVO.getTradeNo(), dcPaymentOrder.getOtherTradeNo(), e);
            response.setMessage(e.getStripeError().getMessage());
        }
        return response;
    }


    /**
     * stripe callback
     *
     * @param json
     * @param reqSignature
     * @return
     */
    @Override
    public boolean stripeCallback(String json, String reqSignature) {
        // Get payment information
        DcPaymentType dcPaymentType = getDcPaymentType(PayChannelTypeEnum.PAY_STRIPE.getCode());

        Stripe.apiKey = dcPaymentType.getPrivateKey();

        Event event;
        try {
            event = Webhook.constructEvent(json, reqSignature, dcPaymentType.getEndpointSecret());
        } catch (SignatureVerificationException e) {
            log.error("StripeCallback signature verification failed:", e);
            throw new GlobalException("StripeCallback signature verification failed");
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            log.error("StripeCallback event serialization failed:", json);
            throw new GlobalException("StripeCallback event serialization failed");
        }
        // Handle the event
        switch (event.getType()) {
            case "invoice.payment_succeeded":
                updatePaymentOrderAndInsertRechargeRecord((Invoice) stripeObject, true);
                return true;
            case "invoice.payment_failed":
                updatePaymentOrderAndInsertRechargeRecord((Invoice) stripeObject, false);
                return true;
            case "charge.refunded":
                queryRefundResult((Charge) stripeObject);
                return true;
            default:
                log.info("stripeCallback Unknown event type:{}", event.getType());
        }
        return false;
    }

    @Override
    public boolean coinbaseCallback(String json) {
        return false;
    }


    @Override
    public boolean queryPayResult(QueryPayResultEntity queryPayResultEntity) {
        return queryPaymentResult(queryPayResultEntity);
    }

    @Override
    public boolean refundResult(QueryPayResultEntity queryPayResultEntity) {
        return queryRefundResult(queryPayResultEntity);
    }

    /**
     * Refund processing
     *
     * @param charge
     * @return
     */
    private boolean queryRefundResult(Charge charge) {
        List<Refund> refunds = charge.getRefunds().getData();
        String refundReceipt = charge.getReceiptUrl();
        for (Refund refund : refunds) {
            if (!updatePaymentOrderAndPaymentRefund(refund, refundReceipt)) {
                return false;
            }
        }
        return true;
    }

    /**
     * stripe Check refund status
     *
     * @param queryPayResultEntity
     * @return
     */
    private boolean queryRefundResult(QueryPayResultEntity queryPayResultEntity) {
        DcPaymentType dcPaymentType = getDcPaymentType(queryPayResultEntity.getChannelCode());
        Stripe.apiKey = dcPaymentType.getPrivateKey();
        try {
            Refund refund = Refund.retrieve(queryPayResultEntity.getOtherTradeNo());
            if (updatePaymentOrderAndPaymentRefund(refund, null)) {
                return refund.getStatus().equalsIgnoreCase("succeeded") ? true : false;
            }
        } catch (StripeException e) {
            log.error("stripe Failed to check refund status:", e);
        }
        return false;
    }

    /**
     * Update payment orders and modify refund status
     * Only refunds initiated by data center background are supported, while refund events initiated by stripe background are not processed
     *
     * @param refund
     * @return
     */
    private boolean updatePaymentOrderAndPaymentRefund(Refund refund, String refundReceipt) {
        if (Objects.nonNull(refund)) {
            String tradeNo = refund.getMetadata().get("tradeNo");
            if (StringUtils.isNotBlank(tradeNo)) {
                // Find out tradeNo according to refund and update paymentOrder
                DcPaymentOrder paymentOrder = dcPaymentOrderMapper.selectOrderByTradeNo(tradeNo);
                if (Objects.isNull(paymentOrder)) {
                    log.error(String.format("stripe refund id:%s,paymentOrder is empty"), refund.getId());
                    return false;
                }
                paymentOrder.setIsRefund(OrderRefundStateEnum.REFUNDED.getCode());
                dcPaymentOrderMapper.updateByPrimaryKey(paymentOrder);

                // Update refund receipt status according to stripe refund ID
                DcPaymentRefund dcPaymentRefund = dcPaymentRefundMapper.selectByOtherTradeNo(refund.getId());
                if (Objects.isNull(dcPaymentRefund)) {
                    String reason = StringUtils.isEmpty(refund.getMetadata().get("refundReason")) ? "" : refund.getMetadata().get("refundReason");
                    long operator = Long.parseLong(StringUtils.isEmpty(refund.getMetadata().get("operator")) ? "0" : refund.getMetadata().get("operator"));
                    String refundTradeNo = CodePrefixEnum.SPARTANREFUND_ + UUIDUtil.generate();
                    insertPaymentRefund(reason, operator, paymentOrder, refundTradeNo, refund);
                } else {
                    refundReceipt = StringUtils.isEmpty(refundReceipt) ? "" : refundReceipt;
                    dcPaymentRefund.setRefundTime(new Date(Long.parseLong(refund.getCreated() + "000")));
                    dcPaymentRefund.setRefundReceipt(refundReceipt);
                    dcPaymentRefund.setRefundState(RefundStateEnum.getEnumByName(refund.getStatus()).getCode());
                    dcPaymentRefund.setUpdateTime(new Date());
                    dcPaymentRefundMapper.updateByPrimaryKey(dcPaymentRefund);
                }
                return true;
            } else {
                log.error(String.format("stripe refund id:%s,trade_no is empty"), refund.getId());
            }
        }
        return false;
    }

    /**
     * Check payment results
     *
     * @param queryPayResultEntity
     * @return
     */
    private boolean queryPaymentResult(QueryPayResultEntity queryPayResultEntity) {
        // Get payment information
        DcPaymentType dcPaymentType = dcPaymentTypeMapper.selectPaymentByCode(queryPayResultEntity.getChannelCode());
        if (Objects.isNull(dcPaymentType)) {
            throw new GlobalException("Payment information is empty");
        }
        String stripePrivateKey = dcPaymentType.getPrivateKey();

        try {
            Stripe.apiKey = stripePrivateKey;
            Invoice invoice = null;
            try {
                invoice = Invoice.retrieve(queryPayResultEntity.getOtherTradeNo());
            } catch (StripeException e) {
                log.error("stripe Check bill status:{} fail:", JSONObject.toJSONString(queryPayResultEntity), e);
            }

            if (ObjectUtils.isNotEmpty(invoice) && invoice.getStatus().equalsIgnoreCase(StripeInvoiceStatusEnum.PAID.getValue())) {
                updatePaymentOrderAndInsertRechargeRecord(invoice, invoice.getPaid());

                // Determine whether the order is expired, if the invalidation status is changed to payment failure
                long currentTime = System.currentTimeMillis() / 1000;
                if (currentTime > invoice.getDueDate()) {
                    log.info("stripe Check bills，bill {} have expired", queryPayResultEntity.getTradeNo());
                    paymentHandler.updateOrderToFail(queryPayResultEntity.getTradeNo());
                    return true;
                }

                return true;
            }
        } catch (Exception e) {
            log.error("stripe Check bill status:{} abnormal:", JSONObject.toJSONString(queryPayResultEntity), e);
        }
        return false;
    }

    /**
     * create PaymentOrder
     */
    private DcPaymentOrder createPaymentOrder(PaymentReqVO paymentReqVO, String tradeNo, Invoice invoice, ChainSalePrice chainSalePrice, DcPaymentType dcPaymentType) {

        // Assemble payment order information into the warehouse
        DcPaymentOrder dcPaymentOrder = DcPaymentOrder.builder()
                .accountAddress(paymentReqVO.getChainAccountAddress())
                .chainId(paymentReqVO.getChainId())
                .salePriceId(chainSalePrice.getSalePriceId())
                .paymentTypeId(dcPaymentType.getPaymentTypeId())
                .tradeNo(tradeNo)
                .otherTradeNo(invoice.getId())
                .paymentIntent(invoice.getPaymentIntent())
                .email(paymentReqVO.getEmail())
                .currency(currency)
                .gasCount(paymentReqVO.getGasCount())
                .payAmount(NumberUtil.round(paymentReqVO.getPayAmount().doubleValue() / 100, 2))
                .payState(PayStateEnum.PENDING.getCode())
                .gasRechargeState(RechargeStateEnum.NO_PROCESSING_REQUIRED.getCode())
                .updateTime(new Date())
                .createTime(new Date())
                .build();
        dcPaymentOrderMapper.insertSelective(dcPaymentOrder);

        return dcPaymentOrder;
    }

    /**
     * Update order payment status
     */
    private synchronized void updatePaymentOrderAndInsertRechargeRecord(Invoice invoice, boolean paid) {
        // Metadata (custom data)
        Map<String, String> metadata = invoice.getMetadata();
        String tradeNo = metadata.get("tradeNo");

        // Check if the order exists
        DcPaymentOrder selectOrderByTradeNo = dcPaymentOrderMapper.selectOrderByTradeNo(tradeNo);
        if (Objects.isNull(selectOrderByTradeNo)) {
            log.error("stripe Payment result query---Order does not exist:{}", tradeNo);
            return;
        }

        // Determine whether the payment is successful
        if (Objects.equals(PayStateEnum.SUCCESS.getCode(), selectOrderByTradeNo.getPayState())) {
            log.info("stripe Payment result query---order：{} Paid successfully", tradeNo);
            return;
        }

        // Assemble order information, update status
        short payState = paid ? PayStateEnum.SUCCESS.getCode() : PayStateEnum.FAILURE.getCode();
        Date payTime = ObjectUtils.isEmpty(invoice.getStatusTransitions().getPaidAt()) ? new Date() : new Date(Long.parseLong(invoice.getStatusTransitions().getPaidAt() + "000"));
        DcPaymentOrder paymentOrder = DcPaymentOrder.builder()
                .orderId(selectOrderByTradeNo.getOrderId())
                .payState(payState)
                .payTime(payTime)
                .updateTime(new Date())
                .gasRechargeState(RechargeStateEnum.PENDING.getCode())
                .build();
        dcPaymentOrderMapper.updateByPrimaryKeySelective(paymentOrder);

        // Check whether the gas recharge record has been added according to orderId. No new gas recharge record has been added, otherwise skip
        if (paid) {
            DcGasRechargeRecord rechargeRecord = dcGasRechargeRecordMapper.selectByOrderId(selectOrderByTradeNo.getOrderId());
            if (ObjectUtils.isEmpty(rechargeRecord)) {
                DcGasRechargeRecord dcGasRechargeRecord = DcGasRechargeRecord.builder()
                        .chainId(selectOrderByTradeNo.getChainId())
                        .orderId(selectOrderByTradeNo.getOrderId())
                        .chainAddress(selectOrderByTradeNo.getAccountAddress())
                        .gas(selectOrderByTradeNo.getGasCount())
                        .state(RechargeSubmitStateEnum.PENDING_SUBMIT.getCode())
                        .updateTime(new Date())
                        .rechargeState(RechargeStateEnum.NO_PROCESSING_REQUIRED.getCode())
                        .build();
                dcGasRechargeRecordMapper.insertRechargeRecord(dcGasRechargeRecord);

                // Send a payment success email
                selectOrderByTradeNo.setPayTime(payTime);
                String customerEmail = metadata.get("customerEmail");
                paymentHandler.sendPaySuccessEmail(selectOrderByTradeNo, customerEmail);

                log.info("stripe Successful payment---gas the deposit record was successfully stored,tradeNo:{}", tradeNo);
            } else {
                log.info("stripe Successful payment---gas the deposit already exists,tradeNo:{}", tradeNo);
            }
        }
    }

    /**
     * Create a stripe customer
     * 1、If already bound, return customerId directly
     * 2、Otherwise, create stripe customer and bind the data center side, returning customerId
     */
    private String createCustomer(String stripePrivateKey, PaymentReqVO paymentReqVO, DcChainAccess dcChainAccess) {
        Stripe.apiKey = stripePrivateKey;

        try {
            String customerId = dcChainAccess.getStripeCustomerId();
            if (StringUtils.isEmpty(customerId)) {

                // ctreate customer
                CustomerCreateParams params = CustomerCreateParams
                        .builder()
                        .setName(paymentReqVO.getChainAccountAddress())
                        .setEmail(paymentReqVO.getEmail())
                        .build();
                customerId = Customer.create(params).getId();

                // update member
                dcChainAccess.setStripeCustomerId(customerId);
                dcChainAccessMapper.updateByPrimaryKeySelective(dcChainAccess);
            }
            return customerId;
        } catch (StripeException e) {
            log.error("StripeCustomer failed to create customer:", e);
            throw new GlobalException("StripeCustomer failed to create customer");
        }
    }

    /**
     * create stripe invoice
     */
    private Invoice createInvoice(String stripePrivateKey, String customerId, PaymentReqVO paymentReqVO, String tradeNo) {
        Stripe.apiKey = stripePrivateKey;

        // Create a subitem
        InvoiceItem invoiceItem;
        try {
            InvoiceItemCreateParams invoiceItemCreateParams = InvoiceItemCreateParams.builder()
                    .setCustomer(customerId)
                    .setCurrency(currency)
                    .setAmount(paymentReqVO.getPayAmount().longValue())
                    .setDescription(paymentReqVO.getRemarks())
                    .build();

            invoiceItem = InvoiceItem.create(invoiceItemCreateParams);
        } catch (StripeException e) {
            log.error("Exception when Stripe creating the bill:", e);
            throw new GlobalException("Exception when Stripe creating the bill");
        }

        // Create a bill
        Invoice invoice = null;
        try {
            // metaData
            Map<String, String> metaData = new HashMap<>();
            metaData.put("customerEmail", paymentReqVO.getEmail());
            metaData.put("tradeNo", tradeNo);

            InvoiceCreateParams invoiceCreateParams = InvoiceCreateParams.builder()
                    .setCustomer(customerId)
                    .setAutoAdvance(true)
                    .setMetadata(metaData)
                    .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
                    .setDueDate(DateUtil.offsetHour(new Date(), cueDate).getTime() / 1000)
                    .setPendingInvoiceItemsBehavior(InvoiceCreateParams.PendingInvoiceItemsBehavior.INCLUDE)
                    .build();
            invoice = Invoice.create(invoiceCreateParams);
        } catch (StripeException e) {
            log.error("stripe createInvoice error:", e);
        }

        // Subitems are removed if the bill creation fails, otherwise they will accumulate
        if (ObjectUtils.isEmpty(invoice)) {
            deleteInvoiceItem(stripePrivateKey, invoiceItem);
            throw new GlobalException("Stripe failed to create the bill");
        }

        // Bill changed to complete status
        invoice = modifyDraftInvoice(stripePrivateKey, invoice);
        return invoice;
    }

    /**
     * Delete the bill subitem
     */
    private void deleteInvoiceItem(String stripePrivateKey, InvoiceItem invoiceItem) {
        Stripe.apiKey = stripePrivateKey;
        try {
            invoiceItem.delete();
        } catch (StripeException e) {
            log.error("Stripe failed to create the invoice, deleteInvoiceItem exception:", e);
            throw new GlobalException("Stripe failed to create the invoice, deleteInvoiceItem exception");
        }
    }

    /**
     * Modify the billing status to complete
     */
    private Invoice modifyDraftInvoice(String stripePrivateKey, Invoice invoice) {
        Stripe.apiKey = stripePrivateKey;
        int modifyNum = 0;
        while (DEFAULT_INVOICE_STATUS.equals(invoice.getStatus()) && modifyNum <= DRAFT_MODIFIED_COUNT) {
            try {
                Thread.sleep(500);
                invoice = invoice.finalizeInvoice();
            } catch (Exception e) {
                log.error("Failed to modify the status of Stripe draft bill:", e);
                throw new GlobalException("Failed to modify the status of Stripe draft bill");
            }
        }
        return invoice;
    }

    /**
     * Get payment information
     *
     * @param chainCode
     * @return
     */
    @NotNull
    private DcPaymentType getDcPaymentType(String chainCode) {
        DcPaymentType dcPaymentType = dcPaymentTypeMapper.selectPaymentByCode(chainCode);
        if (Objects.isNull(dcPaymentType)) {
            throw new GlobalException("Payment information is empty");
        }
        return dcPaymentType;
    }

    /**
     * New refund record
     *
     * @param reason
     * @param operator
     * @param dcPaymentOrder
     * @param refundTradeNo
     * @param refund
     */
    private void insertPaymentRefund(String reason, long operator, DcPaymentOrder dcPaymentOrder, String refundTradeNo, Refund refund) {
        DcPaymentRefund dcPaymentRefund = DcPaymentRefund.builder()
                .refundState(RefundStateEnum.getEnumByName(refund.getStatus()).getCode())
                .orderId(dcPaymentOrder.getOrderId())
                .refundTime(new Date(Long.parseLong(refund.getCreated() + "000")))
                .refundAmount(dcPaymentOrder.getPayAmount())
                .accountAddress(dcPaymentOrder.getAccountAddress())
                .createTime(new Date())
                .remarks(reason)
                .tradeNo(refundTradeNo)
                .accountAddress(dcPaymentOrder.getAccountAddress())
                .otherTradeNo(refund.getId())
                .operator(operator)
                .updateTime(new Date())
                .build();
        dcPaymentRefundMapper.insert(dcPaymentRefund);
    }

}
