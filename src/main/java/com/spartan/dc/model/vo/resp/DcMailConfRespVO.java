package com.spartan.dc.model.vo.resp;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class DcMailConfRespVO {
    private Long maillConfId;

    private String mailHost;

    private String mailPort;

    private String mailUserName;

    private String mailPassWord;

    private String properties;

    private Short type;

    private String accessKey;

    private String secretKey;

    private String regionStatic;
}