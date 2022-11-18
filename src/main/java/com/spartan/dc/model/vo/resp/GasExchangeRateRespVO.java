package com.spartan.dc.model.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author wxq
 * @create 2022/10/19 17:11
 * @description ntt exchange rate
 */
@Data
@Builder
public class GasExchangeRateRespVO {

    @ApiModelProperty(value = "gasCount", required = true)
    private  String gasCount;

    @ApiModelProperty(value = "usPrice（cent）", required = true)
    private  String usPrice;
}
