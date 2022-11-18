package com.spartan.dc.core.easyexcel;


import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DcPaymentRefundExcelEntity {

    @Alias("Refund Order No")
    private String tradeNo;

    @Alias("Refund Transaction No")
    private String  otherTradeNo;

    @Alias("Order No")
    private String dprTradeNo;

    @Alias("Wallet Address")
    private String accountAddress;

    @Alias("Amount")
    private BigDecimal payAmount;

    @Alias("Unit")
    private String currency;

    @Alias("Payment Method")
    private String payType;

    @Alias("Refund Status")
    private String refundState;

    @Alias("Operator")
    private String operator;

    @Alias("Operation Time")
    private String updateTime;

    @Alias("Refund Time")
    private String refundTime;
}