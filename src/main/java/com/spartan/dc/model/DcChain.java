package com.spartan.dc.model;

public class DcChain {
    private Long chainId;

    private String chainType;

    private String chainName;

    private String rechargeUnit;

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType == null ? null : chainType.trim();
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName == null ? null : chainName.trim();
    }

    public String getRechargeUnit() {
        return rechargeUnit;
    }

    public void setRechargeUnit(String rechargeUnit) {
        this.rechargeUnit = rechargeUnit == null ? null : rechargeUnit.trim();
    }
}