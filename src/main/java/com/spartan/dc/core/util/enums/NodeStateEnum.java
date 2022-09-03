package com.spartan.dc.core.util.enums;

/**
 *
 * @author linzijun
 * @version V1.0
 * @date 2022/8/18 19:52
 */
public enum NodeStateEnum {

    //
    TO_THE_NET((short)0, "Pending Registration"),
    //
    IN_THE_SUBMISSION((short)1, "Registration In Progress"),
    //
    IN_THE_INSPECTION((short)2, "Registration Testing"),
    //
    NETWORK_FAIL((short)3, "Registration failed"),
    //
    NETWORK_SUCCESS((short)4, "Registration complete");

    private Short code;
    private String name;

    NodeStateEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static NodeStateEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (NodeStateEnum e : NodeStateEnum.values()) {
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
