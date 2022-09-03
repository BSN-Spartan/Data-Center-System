package com.spartan.dc.model.vo.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author wxq
 * @create 2022/8/15 14:36
 * @description off network
 */
@Data
public class OffNetworkReqVO {
    @NotNull(message = "chainId can not be null")
    @Min(1)
    @Max(3)
    private int chainId;
}
