package com.spartan.dc.core.enums;


public enum UserStateEnum {
    STATE_USE((short) 1, "Enabled"),
    STATE_STOP_USE((short) 0, "Disabled");


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

    UserStateEnum(Short state, String ststeDesc) {
        this.state = state;
        this.ststeDesc = ststeDesc;
    }

    public static String getStateDesc(Short state) {
        for (UserStateEnum temp : UserStateEnum.values()) {
            if (state.equals(temp.getState())) {
                return temp.getStsteDesc();
            }
        }
        return "--";
    }
}
