package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 */
public enum SystemConfIconPathEnum {
    ICON_PATH("icon", "/static/icon");

    private final String code;
    private final String name;

    SystemConfIconPathEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
