package com.spartan.dc.core.dto.spartan;

public class CaptchaReqVO {

    private String reqCode;

    /**
     * email
     */
    private String email;

    public String getReqCode() {
        return reqCode;
    }

    public void setReqCode(String reqCode) {
        this.reqCode = reqCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CaptchaReqVO() {
    }

    public CaptchaReqVO(String reqCode, String email) {
        this.reqCode = reqCode;
        this.email = email;
    }
}
