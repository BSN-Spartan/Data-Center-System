package com.spartan.dc.core.enums;

/**
 * @ClassName MailConfSecretKeyEnum
 * @Author wjx
 * @Date 2023/3/7 15:43
 * @Version 1.0
 */
public enum MailConfSecretKeyEnum {
    // Corresponding secret key
    SPRING_MAIL_KEY("SPRING_MAIL_KEY", "SPRING_MAIL_KEY"),
    AWS_ACCESS_KEY("AWS_ACCESS_KEY", "AWS_ACCESS_KEY"),
    AWS_SECRET_KEY("AWS_SECRET_KEY", "AWS_SECRET_KEY");

    private final String code;
    private final String name;

    MailConfSecretKeyEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MailConfSecretKeyEnum getEnumByCode(String code) {
        if (code == null) {
            return null;
        }
        for (MailConfSecretKeyEnum e : MailConfSecretKeyEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
