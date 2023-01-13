package com.spartan.dc.model;

import java.util.Date;

public class DcUser {
    private Long userId;

    private String contactsEmail;

    private String contactsName;

    private String contactsPhone;

    private Short state;

    private Date createTime;

    private String salt;

    private String password;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContactsEmail() {
        return contactsEmail;
    }

    public void setContactsEmail(String contactsEmail) {
        this.contactsEmail = contactsEmail == null ? null : contactsEmail.trim();
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName == null ? null : contactsName.trim();
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone == null ? null : contactsPhone.trim();
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt == null ? null : salt.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    @Override
    public String toString() {
        return "DcUser{" +
                "userId=" + userId +
                ", contactsEmail='" + contactsEmail + '\'' +
                ", contactsName='" + contactsName + '\'' +
                ", contactsPhone='" + contactsPhone + '\'' +
                ", state=" + state +
                ", createTime=" + createTime +
                ", salt='" + salt + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}