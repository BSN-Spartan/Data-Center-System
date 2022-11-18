package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author : rjx
 * @Date : 2022/11/4 14:05
 **/
@Data
public class GasExchangeRateReqVO {

    @ApiModelProperty(value = "chainId", required = true, example = "1")
    @NotNull(message = "Chain ID cannot be empty")
    private Long chainId;
}
