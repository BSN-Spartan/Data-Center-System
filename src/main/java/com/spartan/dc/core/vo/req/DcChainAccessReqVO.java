package com.spartan.dc.core.vo.req;

import lombok.Data;

/**
 * @ClassName DcChainAccessReqVO
 * @Author wjx
 * @Date 2022/12/19 11:00
 * @Version 1.0
 */
@Data
public class DcChainAccessReqVO {

    private Long chainAccessId;

    private Short state;

    private Integer tps;

    private Integer tpd;

    private String contactsEmail;

    private String accessKey;
}
