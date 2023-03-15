package com.spartan.dc.model.vo.resp;

import cn.hutool.core.date.DateTime;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/14 15:19
 */
@Data
public class RechargeDetailRespVO {

    private Long chainId;

    private String chainName;

    private String rechargeUnit;

    private Long orderId;

    private String chainAddress;

    private BigDecimal gas;

    private String rechargeTime;

    private Short state;

    private String txHash;

    private String updateTime;

    private String rechargeCode;

    private BigDecimal ntt;

    private String rechargeResult;

    private Short rechargeState;

    private Long nonce;

    private Short auditState;

    private String auditor;

    private String auditTime;

    private String createUser;

    private String createTime;

    private String auditRemarks;

    private Short rechargeType;

}
