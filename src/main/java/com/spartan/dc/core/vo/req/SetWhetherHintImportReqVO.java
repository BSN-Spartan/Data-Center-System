package com.spartan.dc.core.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/12/23 15:20
 */
@Data
public class SetWhetherHintImportReqVO {

    @ApiModelProperty(value = "Remind or not: 0: Remind 1:Not remind", required = true)
    private Short whetherHint;

}
