package com.spartan.dc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DcChainAccess {
    private Long chainAccessId;

    private String accessKey;

    private String contactsEmail;

    private Short state;

    private Integer tps;

    private Integer tpd;

    private String stripeCustomerId;

    private Date createTime;

    private Date updateTime;

    private Short notifyState;

    public Long getChainAccessId() {
        return chainAccessId;
    }

    public void setChainAccessId(Long chainAccessId) {
        this.chainAccessId = chainAccessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey == null ? null : accessKey.trim();
    }

    public String getContactsEmail() {
        return contactsEmail;
    }

    public void setContactsEmail(String contactsEmail) {
        this.contactsEmail = contactsEmail == null ? null : contactsEmail.trim();
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public Integer getTps() {
        return tps;
    }

    public void setTps(Integer tps) {
        this.tps = tps;
    }

    public Integer getTpd() {
        return tpd;
    }

    public void setTpd(Integer tpd) {
        this.tpd = tpd;
    }

    public String getStripeCustomerId() {
        return this.stripeCustomerId;
    }

    public void setStripeCustomerId(final String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Short getNotifyState() {
        return notifyState;
    }

    public void setNotifyState(Short notifyState) {
        this.notifyState = notifyState;
    }
}