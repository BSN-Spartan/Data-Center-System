package com.spartan.dc.model;

import java.util.Date;

public class SysDataCenter {
    private Long dcId;

    private String contactsEmail;

    private String nttAccountAddress;

    private String token;

    private String dcCode;

    private Date createTime;

    public Long getDcId() {
        return dcId;
    }

    public void setDcId(Long dcId) {
        this.dcId = dcId;
    }

    public String getContactsEmail() {
        return contactsEmail;
    }

    public void setContactsEmail(String contactsEmail) {
        this.contactsEmail = contactsEmail == null ? null : contactsEmail.trim();
    }

    public String getNttAccountAddress() {
        return nttAccountAddress;
    }

    public void setNttAccountAddress(String nttAccountAddress) {
        this.nttAccountAddress = nttAccountAddress == null ? null : nttAccountAddress.trim();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getDcCode() {
        return dcCode;
    }

    public void setDcCode(String dcCode) {
        this.dcCode = dcCode == null ? null : dcCode.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}