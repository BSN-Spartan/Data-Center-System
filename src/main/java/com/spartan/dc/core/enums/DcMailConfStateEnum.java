package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 * @ClassName Refund or not 0：No refund 1：Refund
 */
public enum DcMailConfStateEnum {
    // mail
    ENABLE((short) 0, "enable"),

    // awsMail
    DISABLE((short) 1, "disable");

    private final Short code;
    private final String name;

    DcMailConfStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcMailConfStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcMailConfStateEnum e : DcMailConfStateEnum.values()) {
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
