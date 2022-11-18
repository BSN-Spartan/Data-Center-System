package com.spartan.dc.core.vo.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DcPaymentOrderReqVO {

    /*
    * Order number
    * */
    private String tradeNo;

    /*
     * Third party transaction number
     * */
    private String  otherTradeNo;

    /*
     * Wallet address
     * */
    private String accountAddress;

    /*
    * Order creation start time
    * */
    private String createStartTime;

    /*
    * Order creation end time
    * */
    private String createEndTime;

    /*
    * Payment start time
    * */
    private String payStartTime;

    /*
    * Payment end time
    * */
    private String payEndTime;

    /*
    * Payment status: 0: Payment in progress 1.: Payment successful 2: Payment failed
    * */
    private Long payState;

    /*
    * gas top-up status: 1: pending 2: processing successful 3: processing failed 4: top-up in progress
    * */
    private Long gasRechargeState;

    /*
    * Whether to refund: 0: not refunded 1: refund
    * */
    private Long isRefund;

}