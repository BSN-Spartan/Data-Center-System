package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 * @ClassName Payment status     0：In progress   1.：Payment successful 2：Payment failed
 */
public enum DcPaymentOrderPayStateEnum {
    // In progress
    DURING_IN_PAYMENT((short)0, "In Progress"),
    // Payment successful
    PAYMENT_SUCCESS((short)1, "Paid"),
    // Payment failed
    PAYMENT_FAILURE((short)2, "Failed");

    private final Short code;
    private final String name;

    DcPaymentOrderPayStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcPaymentOrderPayStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcPaymentOrderPayStateEnum e : DcPaymentOrderPayStateEnum.values()) {
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
