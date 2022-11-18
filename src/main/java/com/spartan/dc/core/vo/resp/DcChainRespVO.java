package com.spartan.dc.core.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DcChainRespVO {

    @ApiModelProperty(value = "chain id", required = true)
    private Long chainId;

    @ApiModelProperty(value = "chain type", required = true)
    private String chainType;

    @ApiModelProperty(value = "chain name", required = true)
    private String chainName;
}