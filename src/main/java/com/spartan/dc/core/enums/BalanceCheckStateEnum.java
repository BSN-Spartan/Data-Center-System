package com.spartan.dc.core.enums;


public enum BalanceCheckStateEnum {
    STATE_USE((short) 1, "Enabled"),
    STOP_USE((short) 0, "Disabled");


    private Short state;


    private String ststeDesc;


    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getStsteDesc() {
        return ststeDesc;
    }

    public void setStsteDesc(String ststeDesc) {
        this.ststeDesc = ststeDesc;
    }

    BalanceCheckStateEnum(Short state, String ststeDesc) {
        this.state = state;
        this.ststeDesc = ststeDesc;
    }

    public static String getStateDesc(Short state) {
        for (BalanceCheckStateEnum temp : BalanceCheckStateEnum.values()) {
            if (state.equals(temp.getState())) {
                return temp.getStsteDesc();
            }
        }
        return "--";
    }
}
