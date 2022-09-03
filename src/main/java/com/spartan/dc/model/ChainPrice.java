package com.spartan.dc.model;

import java.math.BigDecimal;
import java.util.Date;

public class ChainPrice {
    private Long chainPriceId;

    private Long chainId;

    private BigDecimal nttCount;

    private BigDecimal gas;

    private Date createTime;

    public Long getChainPriceId() {
        return chainPriceId;
    }

    public void setChainPriceId(Long chainPriceId) {
        this.chainPriceId = chainPriceId;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public BigDecimal getNttCount() {
        return nttCount;
    }

    public void setNttCount(BigDecimal nttCount) {
        this.nttCount = nttCount;
    }

    public BigDecimal getGas() {
        return gas;
    }

    public void setGas(BigDecimal gas) {
        this.gas = gas;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}