package com.spartan.dc.model;

import java.math.BigDecimal;

public class ChainAccountRechargeMeta {
    private Long rechargeMetaId;

    private String chainAccountAddress;

    private BigDecimal gas;

    private Long deadline;

    private String sign;

    public Long getRechargeMetaId() {
        return rechargeMetaId;
    }

    public void setRechargeMetaId(Long rechargeMetaId) {
        this.rechargeMetaId = rechargeMetaId;
    }

    public String getChainAccountAddress() {
        return chainAccountAddress;
    }

    public void setChainAccountAddress(String chainAccountAddress) {
        this.chainAccountAddress = chainAccountAddress == null ? null : chainAccountAddress.trim();
    }

    public BigDecimal getGas() {
        return gas;
    }

    public void setGas(BigDecimal gas) {
        this.gas = gas;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign == null ? null : sign.trim();
    }
}