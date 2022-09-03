package com.spartan.dc.core.dto.task.resp;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/8/9 15:04
 */
public class GasRechargeEventAnalysisRespVO implements Serializable {


    private String txHash;


    private String rechgID;

    /**
     *
     */
    private String operator;

    /**
     *
     */
    private String receiver;

    /**
     *
     */
    private BigDecimal nttAmt;

    /**
     *
     */
    private BigDecimal engAmt;

    /**
     *
     */
    private Long chainID;


    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getRechgID() {
        return rechgID;
    }

    public void setRechgID(String rechgID) {
        this.rechgID = rechgID;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getNttAmt() {
        return nttAmt;
    }

    public void setNttAmt(BigDecimal nttAmt) {
        this.nttAmt = nttAmt;
    }

    public BigDecimal getEngAmt() {
        return engAmt;
    }

    public void setEngAmt(BigDecimal engAmt) {
        this.engAmt = engAmt;
    }

    public Long getChainID() {
        return chainID;
    }

    public void setChainID(Long chainID) {
        this.chainID = chainID;
    }
}
