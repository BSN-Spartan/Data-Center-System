package com.spartan.dc.core.enums;

/**
 *
 * @author linzijun
 * @version V1.0
 * @date 2023/3/1
 */
public enum RechargeTypeEnum {

    //
    PRESENTER((short)0, "presenter"),
    //
    TO_UP((short)1, "to up");

    private Short code;
    private String name;

    RechargeTypeEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RechargeTypeEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (RechargeTypeEnum e : RechargeTypeEnum.values()) {
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
