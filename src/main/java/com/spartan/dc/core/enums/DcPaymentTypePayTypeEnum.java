package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 * @ClassName Payment type1：Fiat currency 2：Stablecoins   3：Remittance
 */
public enum DcPaymentTypePayTypeEnum {
    // Fiat currency
    NCLT((short)1, "Fiat Money"),
    // Stablecoins
    STABLECOIN((short)2, "Stablecoins"),
    // Remittance
    OFFLINE((short)3, "Remittance");

    private final Short code;
    private final String name;

    DcPaymentTypePayTypeEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcPaymentTypePayTypeEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcPaymentTypePayTypeEnum e : DcPaymentTypePayTypeEnum.values()) {
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
