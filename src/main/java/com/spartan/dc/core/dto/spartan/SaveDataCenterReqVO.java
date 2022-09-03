package com.spartan.dc.core.dto.spartan;

public class SaveDataCenterReqVO extends CaptchaReqVO {

    private String nttAccountAddress;

    private String captcha;

    public String getNttAccountAddress() {
        return nttAccountAddress;
    }

    public void setNttAccountAddress(String nttAccountAddress) {
        this.nttAccountAddress = nttAccountAddress;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
