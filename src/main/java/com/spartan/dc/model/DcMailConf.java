package com.spartan.dc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DcMailConf {
    private Long mailConfId;

    private String mailHost;

    private String mailPort;

    private String mailUserName;

    private String mailPassWord;

    private String properties;

    private Short type;

    private String accessKey;

    private String secretKey;

    private String regionStatic;

    private Short state;

    private Date createTime;

    private Date updateTime;
}