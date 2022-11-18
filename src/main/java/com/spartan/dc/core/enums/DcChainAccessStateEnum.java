package com.spartan.dc.core.enums;

/**
 *
 * @author linzijun
 * @version V1.0
 * @date 2022/11/8 10:30
 */
public enum DcChainAccessStateEnum {

    //
    START_USING((short)1, "start using"),
    //
    BLOCK_UP((short)2, "block up"),
    //
    DELETE((short)3, "delete")
    ;

    private Short code;
    private String name;

    DcChainAccessStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcChainAccessStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcChainAccessStateEnum e : DcChainAccessStateEnum.values()) {
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
