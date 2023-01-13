package com.spartan.dc.core.enums;


public enum SysRoleStateEnum {
    ABLE((short) 1, "Enabled"),
    UNABLE((short) 2, "Disabled");

    private Short code;
    private String msg;

    SysRoleStateEnum(Short code, String msg) {
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
        for (SysRoleStateEnum c : SysRoleStateEnum.values()) {
            if (c.code.equals(code)) {
                return c.msg;
            }
        }
        return null;
    }
}
