package com.spartan.dc.core.enums;

/**
 * @author linzijun
 * @create 2023/2/13
 * @description state
 */
public enum TermsServiceAuditStateEnum {

    //
    AUDIT_HANDLE((short)0, "Pending review"),
    //
    AUDIT_SUCCESS((short)1, "Review passed"),
    //
    AUDIT_FAIL((short)2, "Review failed");

    private final Short code;
    private final String name;

    TermsServiceAuditStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Short getCode() {
        return code;
    }
    public static TermsServiceAuditStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (TermsServiceAuditStateEnum e : TermsServiceAuditStateEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
