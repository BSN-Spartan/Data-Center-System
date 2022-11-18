package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/11/9 11:19
 * @description refund state
 */
public enum OrderRefundStateEnum {
    NO_REFUND((short)0, "No refund"),
    REFUNDED((short)1, "Refunded"),
    ;

    private Short code;
    private String name;

    OrderRefundStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OrderRefundStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (OrderRefundStateEnum e : OrderRefundStateEnum.values()) {
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
