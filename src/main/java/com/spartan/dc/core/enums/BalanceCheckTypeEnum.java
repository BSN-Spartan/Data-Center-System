package com.spartan.dc.core.enums;


public enum BalanceCheckTypeEnum {
    MONITOR_NTT((short) 1, "NTT"),
    MONITOR_GAS((short) 2, "GAS");


    private Short type;


    private String typcDesc;


    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getTypcDesc() {
        return typcDesc;
    }

    public void setTypcDesc(String typcDesc) {
        this.typcDesc = typcDesc;
    }

    BalanceCheckTypeEnum(Short type, String typcDesc) {
        this.type = type;
        this.typcDesc = typcDesc;
    }

    public static String getTypeDesc(Short type) {
        for (BalanceCheckTypeEnum temp : BalanceCheckTypeEnum.values()) {
            if (type.equals(temp.getType())) {
                return temp.getTypcDesc();
            }
        }
        return "--";
    }
}
