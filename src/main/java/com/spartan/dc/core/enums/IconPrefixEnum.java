package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 * @ClassName Refund Status: 0: Refund in progress 1.: Refund successful 2: Refund failed
 */
public enum IconPrefixEnum {
    JPG(".jpg", "data:image/jpg;base64,"),
    PNG(".png", "data:image/png;base64,"),
    JPEG(".jpeg", "data:image/jpeg;base64,"),
    SVG(".svg", "data:image/svg+xml;base64,");

    private final String code;
    private final String name;

    IconPrefixEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static IconPrefixEnum getEnumByCode(String code) {
        if (code == null) {
            return null;
        }
        for (IconPrefixEnum e : IconPrefixEnum.values()) {
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
