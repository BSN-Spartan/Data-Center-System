package com.spartan.dc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DcPaymentOrder {
    private Long orderId;

    private Long chainId;

    private Long salePriceId;

    private Long paymentTypeId;

    private String tradeNo;

    private String otherTradeNo;

    private String paymentIntent;

    private String accountAddress;

    private String email;

    private String currency;

    private String exRates;

    private BigDecimal gasCount;

    private String payAccount;

    private BigDecimal payAmount;

    private Short payState;

    private Date payTime;

    private String txHash;

    private Date txTime;

    private String gasTxHash;

    private Date gasTxTime;

    private Short gasRechargeState;

    private Short isRefund;

    private BigDecimal refundAmount;

    private String remarks;

    private Date updateTime;

    private Date createTime;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public Long getSalePriceId() {
        return salePriceId;
    }

    public void setSalePriceId(Long salePriceId) {
        this.salePriceId = salePriceId;
    }

    public Long getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(Long paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
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

    public String getPaymentIntent() {
        return paymentIntent;
    }

    public void setPaymentIntent(String paymentIntent) {
        this.paymentIntent = paymentIntent == null ? null : paymentIntent.trim();
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress == null ? null : accountAddress.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public String getExRates() {
        return exRates;
    }

    public void setExRates(String exRates) {
        this.exRates = exRates == null ? null : exRates.trim();
    }

    public BigDecimal getGasCount() {
        return gasCount;
    }

    public void setGasCount(BigDecimal gasCount) {
        this.gasCount = gasCount;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount == null ? null : payAccount.trim();
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Short getPayState() {
        return payState;
    }

    public void setPayState(Short payState) {
        this.payState = payState;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public Date getTxTime() {
        return txTime;
    }

    public void setTxTime(Date txTime) {
        this.txTime = txTime;
    }

    public String getGasTxHash() {
        return gasTxHash;
    }

    public void setGasTxHash(String gasTxHash) {
        this.gasTxHash = gasTxHash == null ? null : gasTxHash.trim();
    }

    public Date getGasTxTime() {
        return gasTxTime;
    }

    public void setGasTxTime(Date gasTxTime) {
        this.gasTxTime = gasTxTime;
    }

    public Short getGasRechargeState() {
        return gasRechargeState;
    }

    public void setGasRechargeState(Short gasRechargeState) {
        this.gasRechargeState = gasRechargeState;
    }

    public Short getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(Short isRefund) {
        this.isRefund = isRefund;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
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