package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/14 11:20
 */
@Data
public class RechargeAuditReqVO {

    @NotNull(message = "RechargeRecord Id can not be empty")
    private Long rechargeRecordId;

    @NotNull(message = "Decision can not be empty")
    private Short decision;

    @Length(max = 300, message = "auditRemarks, length limit is 300")
    private String auditRemarks;

}
