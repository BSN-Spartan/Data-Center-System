package com.spartan.dc.core.util.enums;


public enum NttTxEnum {

    //
    NODE_NET_AWARD((short)0, "Node Registration Incentive"),
    //
    BUSINESS_AWARD((short)1, "Service Incentive"),
    //
    GAS_RECHARGE_AWARD((short)3, "Top-Up Gas Credit"),
    //
    BUY_NTT_AWARD((short)4, "Top-Up NTT"),
    //
    GAS_RECHARGE_FAIL_REFUND((short)7, "Refund for failed gas credit recharge"),
    //
    NTT_GET((short)8, "Ntt get"),
    //
    NTT_AIR_DROP((short)9, "Ntt air drop"),
    //
    META_TRANSACTION_FEE((short)10, "Transaction Fee of Meta Transaction");

    private Short code;
    private String name;

    NttTxEnum(Short code, String name) {
        this.code = code;
        this.name = name;
    }

    public static NttTxEnum getEnumByCode(Short code) {
        if (code == null) {
            return null;
        }
        for (NttTxEnum e : NttTxEnum.values()) {
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
