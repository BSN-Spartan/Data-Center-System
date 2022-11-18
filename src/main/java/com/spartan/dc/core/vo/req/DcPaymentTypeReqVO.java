package com.spartan.dc.core.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import java.util.Date;

@Data
public class DcPaymentTypeReqVO {
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

    private Date createTime;

    private Date updateTime;

    private Short enableStatus;
}