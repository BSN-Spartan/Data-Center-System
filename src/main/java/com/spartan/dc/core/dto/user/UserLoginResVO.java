package com.spartan.dc.core.dto.user;

import java.io.Serializable;


public class UserLoginResVO implements Serializable {

    private String successUrl;

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public UserLoginResVO() {
    }

    public UserLoginResVO(String successUrl) {
        this.successUrl = successUrl;
    }
}
