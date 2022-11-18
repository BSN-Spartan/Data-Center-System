package com.spartan.dc.core.enums;

/**
 * Response Status Code Enumeration
 *
 * @author xingjie
 * @date 2019-12-24
 */
public enum MessageTypeEnum {

    // Message Type
    MSG((short) 1, "Webmail"),

    EMAIL((short) 2, "Ema"),
    ;


    private Short code;

    private String message;

    MessageTypeEnum(Short code, String message) {
        this.code = code;
        this.message = message;
    }

    public Short getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
