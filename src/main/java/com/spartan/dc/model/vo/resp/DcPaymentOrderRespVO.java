package com.spartan.dc.model.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DcPaymentOrderRespVO {
    private Long orderId;

    private Long chainId;

    private Long salePriceId;

    private Long paymentTypeId;

    private String tradeNo;

    private String otherTradeNo;

    private String paymentIntent;

    private String accountAddress;

    private String email;

    private String currency;

    private String exRates;

    private BigDecimal gasCount;

    private String payAccount;

    private BigDecimal payAmount;

    private Short payState;

    private Date payTime;

    private String txHash;

    private Date txTime;

    private String gasTxHash;

    private Date gasTxTime;

    private Short gasRechargeState;

    private Short isRefund;

    private BigDecimal refundAmount;

    private String remarks;

    private Short payType;
}