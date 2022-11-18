package com.spartan.dc.core.enums;

/**
 *
 * @author rjx
 * @date 2022/10/10 16:17
 */
public enum ChainPriceStateEnum {

    PENDING((short)1, "Pending review"),
    SUCCESS((short)2, "Approved"),
    FAILURE((short)3, "Review failed"),
    ;

    private Short code;
    private String name;

    ChainPriceStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ChainPriceStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (ChainPriceStateEnum e : ChainPriceStateEnum.values()) {
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
