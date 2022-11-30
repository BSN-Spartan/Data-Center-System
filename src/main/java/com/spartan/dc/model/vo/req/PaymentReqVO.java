package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author : rjx
 * @Date : 2022/10/11 16:39
 **/
@Data
public class PaymentReqVO {

    @ApiModelProperty(value = "Payment type 1: fiat currency 2: stablecoin 3: remittance", required = true, example = "1")
    @NotNull(message = "Payment type cannot be empty")
    private Short payType;

    @ApiModelProperty(value = "channelCode, stripe coinbase offline", required = true, example = "stripe")
    @NotBlank(message = "Channel Code cannot be empty")
    private String channelCode;

    @ApiModelProperty(value = "chainId", required = true, example = "1")
    @NotNull(message = "Chain ID cannot be empty")
    private Long chainId;

    @ApiModelProperty(value = "chainAccountAddress", required = true, example = "0XXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
    @NotBlank(message = "Wallet address cannot be empty")
    private String chainAccountAddress;

    @ApiModelProperty(value = "gasCount", required = true, example = "1000000")
    @NotNull(message = "Number of gas cannot be empty")
    private BigDecimal gasCount;

    @ApiModelProperty(value = "payAmount", required = true, example = "1000")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "email", required = true, example = "xxx@163.com")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @ApiModelProperty(value = "captchaCode", required = true, example = "123456")
    @NotBlank(message = "Verification code can not be empty")
    private String captchaCode;

    @ApiModelProperty(value = "remarks")
    private String remarks;

}
