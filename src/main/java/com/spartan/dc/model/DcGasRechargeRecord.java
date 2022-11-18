package com.spartan.dc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DcGasRechargeRecord {
    private Long rechargeRecordId;

    private Long chainId;

    private Long orderId;

    private String chainAddress;

    private BigDecimal gas;

    private Date rechargeTime;

    private Short state;

    private String txHash;

    private Date updateTime;

    private String rechargeCode;

    private BigDecimal ntt;

    private String rechargeResult;

    private Short rechargeState;

    private Long nonce;

    private String md5Sign;

}