package com.spartan.dc.core.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@Data
public class DcSystemConfRespVO {

    @ApiModelProperty(value = "id", required = true)
    private Long confId;

    @ApiModelProperty(value = "Type 1: Portal information 2: Technical support 3: Contact us 4: Chain information access", required = true)
    private Long type;

    @ApiModelProperty(value = "conf_code", required = true)
    private String confCode;

    @ApiModelProperty(value = "conf_value", required = true)
    private String confValue;

}