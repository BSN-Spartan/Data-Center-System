package com.spartan.dc.model;

import lombok.Data;

import java.util.Date;

@Data
public class DcTermsServiceAudit {

    private Long serviceAuditId;

    private Long creator;

    private Date createTime;

    private Long auditor;

    private Date auditTime;

    private Short auditState;

    private String remark;

    private String termsContent;

}