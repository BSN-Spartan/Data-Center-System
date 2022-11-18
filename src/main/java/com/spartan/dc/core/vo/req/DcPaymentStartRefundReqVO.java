package com.spartan.dc.core.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class DcPaymentStartRefundReqVO {

    /**
     * order id
     */
    @NotNull(message = "Order ID can not be empty")
    @ApiModelProperty(value = "orderId")
    private Long orderId;

    /**
     * Third party transaction number
     */
    @NotBlank(message = "The third party transaction number cannot be empty")
    @Length(max = 50, message = "The length of the third party transaction number exceeds the limit")
    @ApiModelProperty(value = "otherTradeNo")
    private String otherTradeNo;

    /**
     * remark
     */
    @Size(max = 200, message = "remarks (maximum 200 characters)")
    @NotBlank(message = "Refund remarks can not be empty")
    @ApiModelProperty(value = "remarks")
    private String remarks;

    /**
     * ayment type: 1: fiat currency 2: stablecoins 3: remittance
     */
    private Short payType;

    /**
     * Operator
     */
    private Long operator;
}