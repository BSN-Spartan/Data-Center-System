package com.spartan.dc.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class DcPaymentRefund {
    private Long refundId;

    private Long orderId;

    private String tradeNo;

    private String otherTradeNo;

    private String accountAddress;

    private BigDecimal refundAmount;

    private Short refundState;

    private Date refundTime;

    private String refundReceipt;

    private String remarks;

    private Long operator;

    private Date updateTime;

    private Date createTime;

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public String getOtherTradeNo() {
        return otherTradeNo;
    }

    public void setOtherTradeNo(String otherTradeNo) {
        this.otherTradeNo = otherTradeNo == null ? null : otherTradeNo.trim();
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress == null ? null : accountAddress.trim();
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Short getRefundState() {
        return refundState;
    }

    public void setRefundState(Short refundState) {
        this.refundState = refundState;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public String getRefundReceipt() {
        return refundReceipt;
    }

    public void setRefundReceipt(String refundReceipt) {
        this.refundReceipt = refundReceipt == null ? null : refundReceipt.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}