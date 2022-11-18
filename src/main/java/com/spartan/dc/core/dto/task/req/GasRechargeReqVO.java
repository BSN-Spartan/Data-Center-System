package com.spartan.dc.core.dto.task.req;


import java.io.Serializable;
import java.math.BigInteger;


public class GasRechargeReqVO implements Serializable {

    private String sender;

    private String receiver;

    private BigInteger engAmt;

    private Integer chainID;

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public BigInteger getEngAmt() {
        return engAmt;
    }

    public void setEngAmt(BigInteger engAmt) {
        this.engAmt = engAmt;
    }

    public Integer getChainID() {
        return chainID;
    }

    public void setChainID(Integer chainID) {
        this.chainID = chainID;
    }
}
