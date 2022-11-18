package com.spartan.dc.core.enums;

/**
 * Delayed Task Type
 *
 * @author rjx
 * @date 2022/10/10 16:17
 */
public enum DelayedTaskTypeEnum {

    QUERY_PAY_RESULT(1, "Query payment results"),
    QUERY_REFUND_RESULT(2, "Query refund results"),
    ;

    private Integer code;
    private String name;

    DelayedTaskTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelayedTaskTypeEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DelayedTaskTypeEnum e : DelayedTaskTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }
}
