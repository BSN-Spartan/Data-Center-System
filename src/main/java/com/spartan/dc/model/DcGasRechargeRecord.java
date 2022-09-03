package com.spartan.dc.model;

import java.math.BigDecimal;
import java.util.Date;

public class DcGasRechargeRecord {
    private Long rechargeRecordId;

    private Long chainId;

    private String chainAddress;

    private BigDecimal gas;

    private Date rechargeTime;

    private Short state;

    private String txHash;

    private Date updateTime;

    private String rechargeCode;

    private BigDecimal ntt;

    private String rechargeResult;

    private Short rechargeState;

    private Long nonce;

    private String md5Sign;

    public String getMd5Sign() {
        return md5Sign;
    }

    public void setMd5Sign(String md5Sign) {
        this.md5Sign = md5Sign;
    }

    public Long getRechargeRecordId() {
        return rechargeRecordId;
    }

    public void setRechargeRecordId(Long rechargeRecordId) {
        this.rechargeRecordId = rechargeRecordId;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getChainAddress() {
        return chainAddress;
    }

    public void setChainAddress(String chainAddress) {
        this.chainAddress = chainAddress == null ? null : chainAddress.trim();
    }

    public BigDecimal getGas() {
        return gas;
    }

    public void setGas(BigDecimal gas) {
        this.gas = gas;
    }

    public Date getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRechargeCode() {
        return rechargeCode;
    }

    public void setRechargeCode(String rechargeCode) {
        this.rechargeCode = rechargeCode == null ? null : rechargeCode.trim();
    }

    public BigDecimal getNtt() {
        return ntt;
    }

    public void setNtt(BigDecimal ntt) {
        this.ntt = ntt;
    }

    public String getRechargeResult() {
        return rechargeResult;
    }

    public void setRechargeResult(String rechargeResult) {
        this.rechargeResult = rechargeResult == null ? null : rechargeResult.trim();
    }

    public Short getRechargeState() {
        return rechargeState;
    }

    public void setRechargeState(Short rechargeState) {
        this.rechargeState = rechargeState;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }
}