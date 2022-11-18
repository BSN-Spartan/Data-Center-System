package com.spartan.dc.core.enums;

/**
 * Payment method
 *
 * @author rjx
 * @date 2022/10/10 16:17
 */
public enum PayChannelTypeEnum {

    PAY_STRIPE("STRIPE", "stripe"),
    PAY_COINBASE("COINBASE", "coinbase"),
    PAY_OFFLINE("OFFLINE", "offline"),

    ;

    private String code;
    private String name;

    PayChannelTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PayChannelTypeEnum getEnumByCode(String code) {
        if (code == null) {
            return null;
        }
        for (PayChannelTypeEnum e : PayChannelTypeEnum.values()) {
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
