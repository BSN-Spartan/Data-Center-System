package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 * @ClassName
 */
public enum DcPaymentTypeEnableStatusEnum {
    //
    ENABLE((short)0, "enable"),
    //
    DISABLE((short)1, "disable");

    private final Short code;
    private final String name;

    DcPaymentTypeEnableStatusEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcPaymentTypeEnableStatusEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcPaymentTypeEnableStatusEnum e : DcPaymentTypeEnableStatusEnum.values()) {
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
