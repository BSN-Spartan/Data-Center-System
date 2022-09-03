package com.spartan.dc.core.dto.task.req;

import lombok.Data;

/**
 * @author wxq
 * @create 2022/8/22 16:06
 * @description query gas recharge record req
 */
@Data
public class GasRechargeRecordReqVO {
    private Short state;
    private Short rechargeState;
    private Short rechargeRecordId;
}
