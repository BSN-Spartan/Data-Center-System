package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 * @ClassName Refund status    0：In progress   1.：Refund successful 2：Refund failed
 */
public enum DcPaymentRefundRefundStateEnum {

	// In progress
    REFUND_OF((short)0, "In Progress"),
    
	// Refund successful
    REFUND_SUCCESS((short)1, "Refunded"),
    
	// Refund failed
    REFUND_FAILURE((short)2, "Failed");

    private final Short code;
    private final String name;

    DcPaymentRefundRefundStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcPaymentRefundRefundStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcPaymentRefundRefundStateEnum e : DcPaymentRefundRefundStateEnum.values()) {
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
