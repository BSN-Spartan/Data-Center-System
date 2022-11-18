package com.spartan.dc.core.vo.req;

import lombok.Data;

@Data
public class DcPaymentRegistrationReqVO {

    /**
     * order id
     */
    private Long orderId;

    /**
     * remark
     */
    private String remarks;
}