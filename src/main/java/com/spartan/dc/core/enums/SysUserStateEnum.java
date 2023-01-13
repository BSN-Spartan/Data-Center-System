package com.spartan.dc.core.enums;


public enum SysUserStateEnum {
    ABLE((short) 1, "Enabled"),
    UNABLE((short) 0, "Disabled");

    private Short code;
    private String msg;

    SysUserStateEnum(Short code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Short getCode() {
        return code;
    }

    public void setCode(Short code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getMsg(Short code) {
        for (SysUserStateEnum c : SysUserStateEnum.values()) {
            if (c.code.equals(code)) {
                return c.msg;
            }
        }
        return null;
    }
}
