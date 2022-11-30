package com.spartan.dc.core.vo.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Data
public class DcPaymentOrderExcelRespVO {

    private String tradeNo;

    private String accountAddress;

    private String chainName;

    private BigDecimal payAmount;

    private BigDecimal gasCount;

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

    private String createTime;

    private Short refundState;

    private Short isRefund;

    private String remarks;

    private String currency;
}