package com.spartan.dc.core.util.enums;

/**
 * ResultDTO
 *
 * @author xingjie
 * @date 2019-12-24
 */
public enum SysRespCodeEnum {

    SUCCESS("0000", "SUCCESS");

    private final String code;

    private final String message;

    SysRespCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public static String getApiResultStateEnum(String code) {
        for (SysRespCodeEnum c : SysRespCodeEnum.values()) {
            if (c.code.equals(code)) {
                return c.getMessage();
            }
        }
        return null;
    }

}
