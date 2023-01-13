package com.spartan.dc.core.enums;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/12/23 15:08
 */
public enum WhetherPromptImportEnum {

    /**
     * prompt
     */
    PROMPT((short) 0, "PROMPT"),

    /**
     * No prompt
     */
    NO_PROMPT((short) 1, "No prompt");

    private Short code;
    private String name;

    WhetherPromptImportEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WhetherPromptImportEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (WhetherPromptImportEnum e : WhetherPromptImportEnum.values()) {
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
