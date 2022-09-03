package com.spartan.dc.model.vo.req;


import lombok.Data;


import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * @author wxq
 * @create 2022/8/9 16:43
 * @description recharge
 */
@Data
public class RechargeReqVO {

    @NotNull(message = "Please select chain type")
    @Min(1)
    @Max(3)
    private int chainId;

    @NotBlank(message = "chainAddress can not be empty")
    @Size(max = 60, message = "chainAddress (maximum 60 characters)")
    private String chainAddress;

    @Digits(integer = 10, fraction = 0)
    private int rechargeType;

    @NotNull(message = "gas can not be null")
    private BigDecimal gas;

    private String password;
}
