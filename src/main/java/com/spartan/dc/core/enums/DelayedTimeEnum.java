package com.spartan.dc.core.enums;

/**
 * Deferred Queue Time Policy
 *
 * @author rjx
 * @date 2022/10/10 16:17
 */
public enum DelayedTimeEnum {

    ONE(1, 60L),
    TWO(2, 2L),
    THREE(3, 5L),
    FOUR(4, 10L),
    FIVE(5, 30L),
    SIX(6, 60L),
    SEVEN(7, 120L),
    EIGHT(8, 300L),
    NINE(9, 600L),
    TEN(10, 1800L),
    ;

    private Integer code;
    private Long value;

    DelayedTimeEnum(Integer code, Long value) {
        this.code = code;
        this.value = value;
    }

    public static DelayedTimeEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DelayedTimeEnum e : DelayedTimeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    public Long getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }
}
