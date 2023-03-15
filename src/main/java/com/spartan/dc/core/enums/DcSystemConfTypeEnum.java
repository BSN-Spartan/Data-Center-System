package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 */
public enum DcSystemConfTypeEnum {
    PORTAL_INFORMATION((short) 1, "Portal information"),
    TECHNICAL_SUPPORT((short) 2, "Technical support"),
    CONTACT_US((short) 3, "Contact us"),
    CHAIN_INFORMATION_ACCESS((short) 4, "Chain access information"),
    KEYSTORE((short)5, "Keystore"),

    TREATY((short)6, "Treaty"),

    NTT_BALANCE_REMINDER((short) 7, "Ntt balance reminder"),
    GAS_BALANCE_REMINDER((short) 8, "Gas balance reminder");


    private final Short code;
    private final String name;

    DcSystemConfTypeEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Short getCode() {
        return code;
    }
}
