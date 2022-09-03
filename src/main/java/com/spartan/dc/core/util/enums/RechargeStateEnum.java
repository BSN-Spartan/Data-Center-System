package com.spartan.dc.core.util.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:19
 * @description state
 */
public enum RechargeStateEnum {
    PENDING((short) 0, "In Progress"),
    SUCCESSFUL((short) 1, "Success"),
    FAILED((short) 2, "Failed"),
    NO_PROCESSING_REQUIRED((short) 3, "Pending");
    private final Short code;
    private final String name;

    RechargeStateEnum(Short code, String name) {
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
