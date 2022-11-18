package com.spartan.dc.model;

import lombok.Data;

import java.util.Date;
@Data
public class DcPaymentType {
    private Long paymentTypeId;

    private Short payType;

    private String payChannelName;

    private String channelCode;

    private String privateKey;

    private String endpointSecret;

    private String apiKey;

    private String apiVersion;

    private String bankName;

    private String bankAccount;

    private String bankAddress;

    private String swiftCode;

    private Short enableStatus;

    private Date createTime;

    private Date updateTime;
}