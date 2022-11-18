package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author : rjx
 * @Date : 2022/10/18 18:16
 **/
@Data
public class CalcGasPriceReqVO {

    @ApiModelProperty(value = "chainAccountAddress", required = true, example = "OXxxxxxxxxxxxxxxxxxxxxxxx")
    @NotBlank(message = "Wallet address cannot be empty")
    private String chainAccountAddress;

    @NotNull(message = "Chain ID cannot be empty")
    @ApiModelProperty(value = "chainId", required = true, example = "1")
    private Long chainId;

    @NotNull(message = "Amount of gas cannot be empty")
    @ApiModelProperty(value = "gasCount", required = true, example = "10000000")
    private BigDecimal gasCount;
}
