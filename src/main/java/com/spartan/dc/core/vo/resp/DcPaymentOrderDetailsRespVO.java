package com.spartan.dc.core.vo.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class DcPaymentOrderDetailsRespVO {

    private String tradeNo;

    private String chainName;

    private String accountAddress;

    private BigDecimal payAmount;

    private String currency;

    private String gasCount;

    private String rechargeUnit;

    private String  otherTradeNo;

    private Short  payType;

    private String payTime;

    private Short payState;

    private String txHash;

    private Short gasRechargeState;

    private String gasTxTime;

    private String gasTxHash;

    private String dprTradeNo;

    private String dprOtherTradeNo;

    private String operator;

    private String operationTime;

    private String refundTime;

    private Short refundState;

    private Short isRefund;

    private String remarks;

    private String payChannelName;
}