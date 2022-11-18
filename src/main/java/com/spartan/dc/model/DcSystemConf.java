package com.spartan.dc.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Builder
public class DcSystemConf {

    private Long confId;

    private Short type;

    private String confCode;

    private String confValue;

    private Date updateTime;
}