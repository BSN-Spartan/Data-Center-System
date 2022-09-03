package com.spartan.dc.model.vo.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wxq
 * @create 2022/8/30 19:45
 * @description get block number
 */
@Data
public class GetBlockNumberReqVO {
    @NotBlank(message = "RPC URL can not be empty")
    private String rpcAddr;

    @NotNull(message = "chainId can not be null")
    @Min(1)
    @Max(3)
    private Short chainId;
}
