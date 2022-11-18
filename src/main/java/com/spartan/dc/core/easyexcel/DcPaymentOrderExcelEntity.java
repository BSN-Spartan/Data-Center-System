package com.spartan.dc.core.easyexcel;


import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DcPaymentOrderExcelEntity {

    @Alias("Order No")
    private String tradeNo;

    @Alias("Third Party Tx No")
    private String otherTradeNo;

    @Alias("Wallet Address")
    private String accountAddress;

    @Alias("Payment Amount")
    private BigDecimal payAmount;

    @Alias("Unit")
    private String currency;

    @Alias("Payment Method")
    private String payType;

    @Alias("Payment Status")
    private String payState;

    @Alias("Gas Credit Payment Status")
    private String gasRechargeState;

    @Alias("Refund")
    private String isRefund;

    @Alias("Created Time")
    private String createTime;

    @Alias("Paid Time")
    private String payTime;
}