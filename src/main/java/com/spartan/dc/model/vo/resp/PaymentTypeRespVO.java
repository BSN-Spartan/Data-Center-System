package com.spartan.dc.model.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : rjx
 * @Date : 2022/10/18 14:58
 **/
@Data
public class PaymentTypeRespVO {

    @ApiModelProperty(value = "paymentTypeId")
    private Long paymentTypeId;

    @ApiModelProperty(value = "payType")
    private Short payType;

    @ApiModelProperty(value = "payChannelName    stripe 、coinbase")
    private String payChannelName;

    @ApiModelProperty(value = "channelCode")
    private String channelCode;

}
