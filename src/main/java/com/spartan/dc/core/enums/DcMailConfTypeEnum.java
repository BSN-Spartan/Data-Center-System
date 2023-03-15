package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 * @ClassName Refund or not 0：No refund 1：Refund
 */
public enum DcMailConfTypeEnum {
    // mail
    MAIL((short) 1, "mail"),

    // awsMail
    AWSMAIL((short) 2, "awsMail");

    private final Short code;
    private final String name;

    DcMailConfTypeEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcMailConfTypeEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcMailConfTypeEnum e : DcMailConfTypeEnum.values()) {
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
