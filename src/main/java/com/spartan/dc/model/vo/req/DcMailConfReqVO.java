package com.spartan.dc.model.vo.req;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName DcMailConfReqVO
 * @Author wjx
 * @Date 2023/3/7 10:40
 * @Version 1.0
 */
@Data
public class DcMailConfReqVO {
    private String mailHost;

    private String mailPort;

    private String mailUserName;

    private String mailPassWord;

    private String properties;

    private Short type;

    private String accessKey;

    private String secretKey;

    private String regionStatic;

    private String receiver;

    private String cc;
}
