package com.spartan.dc.model;

import java.math.BigDecimal;
import java.util.Date;

public class DcNode {
    private Long nodeId;

    private Long chainId;

    private String nodeCode;

    private String rpcAddress;

    private String nodeAddress;

    private String applySign;

    private Short state;

    private String remarks;

    private Date createTime;

    private String txHash;

    private Date txTime;

    private Date applyResultTime;

    private String applyResultTxHash;

    private BigDecimal nttCount;

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode == null ? null : nodeCode.trim();
    }

    public String getRpcAddress() {
        return rpcAddress;
    }

    public void setRpcAddress(String rpcAddress) {
        this.rpcAddress = rpcAddress == null ? null : rpcAddress.trim();
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress == null ? null : nodeAddress.trim();
    }

    public String getApplySign() {
        return applySign;
    }

    public void setApplySign(String applySign) {
        this.applySign = applySign == null ? null : applySign.trim();
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Date getApplyResultTime() {
        return applyResultTime;
    }

    public void setApplyResultTime(Date applyResultTime) {
        this.applyResultTime = applyResultTime;
    }

    public String getApplyResultTxHash() {
        return applyResultTxHash;
    }

    public void setApplyResultTxHash(String applyResultTxHash) {
        this.applyResultTxHash = applyResultTxHash == null ? null : applyResultTxHash.trim();
    }

    public BigDecimal getNttCount() {
        return nttCount;
    }

    public void setNttCount(BigDecimal nttCount) {
        this.nttCount = nttCount;
    }
}