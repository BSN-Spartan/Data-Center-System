package com.spartan.dc.core.vo.resp;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class DcPaymentRefundExcelRespVO {

    private String tradeNo;

    private String  otherTradeNo;

    private String dprTradeNo;

    private String accountAddress;

    private BigDecimal payAmount;

    private String currency;

    private Short payType;

    private Short refundState;

    private String operator;

    private String updateTime;

    private String refundTime;
}