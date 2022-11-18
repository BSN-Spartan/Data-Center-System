package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 13:53
 */
@Data
public class RemittanceRegisterReqVO implements Serializable {

    @NotNull(message = "Order ID can not be empty")
    @ApiModelProperty(value = "orderId")
    private Long orderId;

    @NotBlank(message = "The third party transaction number cannot be empty")
    @Length(max = 50, message = "The length of the third party transaction number exceeds the limit")
    @ApiModelProperty(value = "otherTradeNo")
    private String otherTradeNo;

    @NotBlank(message = "Comment cannot be empty")
    @Length(max = 500, message = "Comment length exceeds limit")
    @ApiModelProperty(value = "remarks")
    private String remarks;

}
