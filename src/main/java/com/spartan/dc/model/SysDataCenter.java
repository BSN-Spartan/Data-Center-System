package com.spartan.dc.model;

import lombok.Data;

import java.util.Date;
@Data
public class SysDataCenter {
    private Long dcId;

    private String contactsEmail;

    private String nttAccountAddress;

    private String token;

    private String dcCode;

    private String logo;

    private Date createTime;
}