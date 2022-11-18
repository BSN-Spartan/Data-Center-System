package com.spartan.dc.model.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author : rjx
 * @Date : 2022/10/18 18:25
 **/
@Data
@Builder
public class CalcGasPriceRespVO {

    @ApiModelProperty(value = "chainAccountAddress", required = true)
    private String chainAccountAddress;

    @ApiModelProperty(value = "gasCount", required = true)
    private BigDecimal gasCount;

    @ApiModelProperty(value = "payAmount（cent）", required = true)
    private Long payAmount;

}
