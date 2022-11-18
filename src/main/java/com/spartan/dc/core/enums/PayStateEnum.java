package com.spartan.dc.core.enums;

/**
 * Payment status
 *
 * @author rjx
 * @date 2022/10/10 16:17
 */
public enum PayStateEnum {

    PENDING((short)0, "In progress"),
    SUCCESS((short)1, "Payment successsul"),
    FAILURE((short)2, "Payment failed"),
    ;

    private Short code;
    private String name;

    PayStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PayStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (PayStateEnum e : PayStateEnum.values()) {
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
