package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : rjx
 * @Date : 2022/10/21 14:00
 **/
@Data
public class QueryPayResultReqVO {

    @ApiModelProperty(value = "orderId", required = true, example = "1")
    private Long orderId;
}
