package com.spartan.dc.core.util.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 */
public enum ChainTypeEnum {
    ETH((short) 1, "ETH"),
    COSMOS((short) 2, "COSMOS"),
    POLYGON((short) 3, "POLYGON");
    private final Short code;
    private final String name;

    ChainTypeEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Short getCode() {
        return code;
    }
}
