package com.spartan.dc.model.vo.req;


import lombok.Data;

import javax.validation.constraints.*;

/**
 * @author wxq
 * @create 2022/8/9 16:43
 * @description recharge
 */
@Data
public class AddChainSalePriceReqVO {

    @NotNull(message = "Please select chain type")
    @Min(1)
    @Max(3)
    private Long chainId;

    @NotBlank(message = "salePrice can not be empty")
    private String salePrice;

    @NotBlank(message = "startDate can not be empty")
    private String startDate;

    private String endDate;
}
