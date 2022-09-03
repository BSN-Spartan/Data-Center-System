package com.spartan.dc.model;

import java.math.BigDecimal;
import java.util.Date;

public class NttTxRecord {
    private Long txRecordId;

    private String txHash;

    private String operator;

    private Short type;

    private String fromAddress;

    private String toAddress;

    private BigDecimal nttCount;

    private BigDecimal fromNttBalance;

    private BigDecimal toNttBalance;

    private Date txTime;

    private Date createTime;

    private String md5Sign;

    public String getMd5Sign() {
        return md5Sign;
    }

    public void setMd5Sign(String md5Sign) {
        this.md5Sign = md5Sign;
    }

    public Long getTxRecordId() {
        return txRecordId;
    }

    public void setTxRecordId(Long txRecordId) {
        this.txRecordId = txRecordId;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress == null ? null : fromAddress.trim();
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress == null ? null : toAddress.trim();
    }

    public BigDecimal getNttCount() {
        return nttCount;
    }

    public void setNttCount(BigDecimal nttCount) {
        this.nttCount = nttCount;
    }

    public BigDecimal getFromNttBalance() {
        return fromNttBalance;
    }

    public void setFromNttBalance(BigDecimal fromNttBalance) {
        this.fromNttBalance = fromNttBalance;
    }

    public BigDecimal getToNttBalance() {
        return toNttBalance;
    }

    public void setToNttBalance(BigDecimal toNttBalance) {
        this.toNttBalance = toNttBalance;
    }

    public Date getTxTime() {
        return txTime;
    }

    public void setTxTime(Date txTime) {
        this.txTime = txTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}