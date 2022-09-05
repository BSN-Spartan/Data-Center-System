package com.spartan.dc.core.dto.dc;

import lombok.Data;


@Data
public class DataCenter {
    private String contactsEmail;
    private String nttAccountAddress;
    private String token;
    private String dcCode;
    private String createTime;
}