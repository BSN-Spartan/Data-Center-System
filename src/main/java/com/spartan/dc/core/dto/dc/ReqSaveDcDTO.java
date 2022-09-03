package com.spartan.dc.core.dto.dc;

/**
 * Desc：
 *
 * @Created by 2022-07-21 13:36
 */
public class ReqSaveDcDTO {

    private String email;

    private String code;

    private String nttAccountAddress;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNttAccountAddress() {
        return nttAccountAddress;
    }

    public void setNttAccountAddress(String nttAccountAddress) {
        this.nttAccountAddress = nttAccountAddress;
    }
}
