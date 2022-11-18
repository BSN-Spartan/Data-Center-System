package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 * @ClassName Refund or not 0：No refund 1：Refund
 */
public enum DcPaymentOrderIsRefundEnum {
    // No refund
    NOT_REFUND((short)0, "No Refund"),
    // Refund
    HAVEREFUND((short)1, "Refunded");

    private final Short code;
    private final String name;

    DcPaymentOrderIsRefundEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcPaymentOrderIsRefundEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcPaymentOrderIsRefundEnum e : DcPaymentOrderIsRefundEnum.values()) {
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
