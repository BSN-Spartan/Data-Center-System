package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/11/8 16:03
 * @description refund state
 */
public enum RefundStateEnum {
    PENDING((short)0, "pending"),
    SUCCESS((short)1, "succeeded"),
    ERROR((short)2, "failed"),
    REQUIRES_ACTION((short)3, "requires_action"),
    CANCELED((short)4, "canceled");

    private Short code;
    private String name;

    RefundStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RefundStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (RefundStateEnum e : RefundStateEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    public static RefundStateEnum getEnumByName(String name) {
        if (name == null) {
            return null;
        }
        for (RefundStateEnum e : RefundStateEnum.values()) {
            if (e.name.equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Short getCode() {
        return code;
    }
}
