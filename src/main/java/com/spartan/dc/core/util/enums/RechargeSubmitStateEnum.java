package com.spartan.dc.core.util.enums;

/**
 * @author wxq
 * @create 2022/8/9 19:25
 * @description recharge state
 */
public enum RechargeSubmitStateEnum {
    PENDING_SUBMIT((short) 1, "PENDING_SUBMIT"),
    SUBMITTING((short) 2, "SUBMITTING"),
    SUBMITTED_SUCCESSFULLY((short) 3, "SUCCESS"),
    SUBMISSION_FAILED((short) 4, "FAILED");
    private final Short code;
    private final String name;

    RechargeSubmitStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Short getCode() {
        return code;
    }

}
