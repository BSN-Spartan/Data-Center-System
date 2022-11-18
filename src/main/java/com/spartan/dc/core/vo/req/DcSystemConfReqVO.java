package com.spartan.dc.core.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Data
public class DcSystemConfReqVO {
    @ApiModelProperty(value = "conf_code", required = true)
    private String confCode;

    @ApiModelProperty(value = "conf_value", required = true)
    @Max(200)
    private String confValue;
}