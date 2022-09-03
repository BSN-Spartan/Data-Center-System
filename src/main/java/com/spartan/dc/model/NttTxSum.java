package com.spartan.dc.model;

import java.math.BigDecimal;

public class NttTxSum {
    private Long nttTxSumId;

    private BigDecimal flowIn;

    private BigDecimal flowOut;

    public Long getNttTxSumId() {
        return nttTxSumId;
    }

    public void setNttTxSumId(Long nttTxSumId) {
        this.nttTxSumId = nttTxSumId;
    }

    public BigDecimal getFlowIn() {
        return flowIn;
    }

    public void setFlowIn(BigDecimal flowIn) {
        this.flowIn = flowIn;
    }

    public BigDecimal getFlowOut() {
        return flowOut;
    }

    public void setFlowOut(BigDecimal flowOut) {
        this.flowOut = flowOut;
    }
}