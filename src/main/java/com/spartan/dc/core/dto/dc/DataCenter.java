package com.spartan.dc.core.dto.dc;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DataCenter {
    private String contactsEmail;
    private String nttAccountAddress;
    private String token;
    private String dcCode;
    private String createTime;
}