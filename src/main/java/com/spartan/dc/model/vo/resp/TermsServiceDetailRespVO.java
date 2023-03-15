package com.spartan.dc.model.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/15 11:14
 */
@Data
public class TermsServiceDetailRespVO {

    private Long serviceAuditId;

    private String creator;

    private String createTime;

    private String auditor;

    private String auditTime;

    private Short auditState;

    private String remark;

    private String termsContent;

}
