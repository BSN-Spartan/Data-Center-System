package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author wxq
 * @create 2022/11/8 14:00
 * @description refund
 */
@Data
public class RefundReqVO {
    @ApiModelProperty(value = "trade No")
    @NotBlank(message = "tradeNo can not be empty")
    private String tradeNo;

    @Size(max = 200, message = "reason (maximum 200 characters)")
    @NotBlank(message = "Refund reason can not be empty")
    @ApiModelProperty(value = "Refund reason")
    private String reason;

    @ApiModelProperty(value = "Channel ID stripe coinbase offline", required = true, example = "stripe")
    @NotBlank(message = "Channel ID cannot be empty")
    private String channelCode;
}
