package com.spartan.dc.core.enums;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/8 13:45
 */
public enum DcChainGatewayType {

    //
    NONSUPPORT((short)0, "nonsupport"),
    //
    SUPPORT((short)1, "support")
    ;

    private Short code;
    private String name;

    DcChainGatewayType(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DcChainGatewayType getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (DcChainGatewayType e : DcChainGatewayType.values()) {
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
