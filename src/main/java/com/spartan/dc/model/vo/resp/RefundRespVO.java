package com.spartan.dc.model.vo.resp;

import lombok.Data;

/**
 * @author wxq
 * @create 2022/11/8 14:15
 * @description refund resp
 */
@Data
public class RefundRespVO {
    private Short status;
    private String otherTradeNo;
    private String tradeNo;
    private String message;
}
