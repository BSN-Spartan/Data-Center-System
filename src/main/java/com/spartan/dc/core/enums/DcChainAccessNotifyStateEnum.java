package com.spartan.dc.core.enums;

/**
 *
 * @author linzijun
 * @version V1.0
 * @date 2022/11/8 10:30
 */
public enum DcChainAccessNotifyStateEnum {

    //
    NOTIFY_SUCCESS((short)1, "notify Success"),
    //
    NOTIFY_FAILURE((short)2, "notify failure")
    ;

    private Short code;
    private String name;

    DcChainAccessNotifyStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcChainAccessNotifyStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcChainAccessNotifyStateEnum e : DcChainAccessNotifyStateEnum.values()) {
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
