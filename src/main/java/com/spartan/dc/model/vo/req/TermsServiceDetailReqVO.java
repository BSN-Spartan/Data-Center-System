package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/15 11:14
 */
@Data
public class TermsServiceDetailReqVO {

    @NotNull(message = "serviceAuditId cannot be empty")
    private Long serviceAuditId;

}
