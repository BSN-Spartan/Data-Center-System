package com.spartan.dc.delayed.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @Author : rjx
 * @Date : 2022/10/11 14:00
 **/
@Data
@SuperBuilder
public class RefundResultEntity extends DelayedQueueEntity {

    private Short payType;

    private String channelCode;

    private String tradeNo;

    private String otherTradeNo;
}
