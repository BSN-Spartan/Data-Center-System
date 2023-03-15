package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/15 11:14
 */
@Data
public class TermsServiceAuditReqVO {

    @NotNull(message = "ServiceAuditId cannot be empty")
    private Long serviceAuditId;

    @NotNull(message = "decision cannot be empty")
    private Short decision;

    @Length(max = 300, message = "auditRemarks, length limit is 300")
    private String remark;

}
