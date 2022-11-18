package com.spartan.dc.core.enums;

/**
 * node state
 * @author linzijun
 * @version V1.0
 * @date 2022/8/1 14:17
 */
public enum ApplyNodeStateEnum {

    APPLY_PENDING((short) 0, "Pending Registration"),
    APPLY_SUBMIT((short) 1, "Registration In Progress"),
    APPLY_CHECKING((short) 2, "Registration Testing"),
    APPLY_FAILED((short) 3, "Registration failed"),
    APPLY_SUCCESS((short) 4, "Registration complete");

    private Short code;
    private String name;

    ApplyNodeStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ApplyNodeStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (ApplyNodeStateEnum e : ApplyNodeStateEnum.values()) {
            if (e.code.equals(code)) {
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
