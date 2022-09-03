package com.spartan.dc.core.dto.dc;

import lombok.Data;

/**
 * @author wxq
 * @create 2022/8/22 15:59
 * @description gas recharge record
 */
@Data
public class GasRechargeRecordDTO {
    private String rechargeRecordId;
    private String txHash;
    private Short state;
    private Short rechargeState;
}
