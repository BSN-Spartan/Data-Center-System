package com.spartan.dc.core.vo.req;

import lombok.Data;

@Data
public class DcPaymentRefundReqVO {

    /*
    * Refund order number
    * */
    private String tradeNo;

    /*
     * Refund transaction number
     * */
    private String  otherTradeNo;

    /*
     * Order number
     * */
    private String dprTradeNo;

    /*
    * Wallet address
    * */
    private String accountAddress;

    /*
    * Operation start time
    * */
    private String updateStartTime;

    /*
    * Operation end time
    * */
    private String updateEndTime;

    /*
    * Refund start time
    * */
    private String refundStartTime;

    /*
    * Refund end time
    * */
    private String refundEndTime;

    /*
    * Refund Status: 0: Refund in progress 1.: Refund successful 2: Refund failed
    * */
    private Long refundState;

    /*
     * Channel ID
     * */
    private String channelCode;


}