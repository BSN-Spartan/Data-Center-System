package com.spartan.dc.model.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceReminderRespVo {
    private Long accountBalanceConfId;

    private String chainName;

    private String chainAddress;

    private Short monitorType;

    private BigDecimal balanceLimit;

    private String reminderEmail;

    private Short state;

    private Date createTime;

    public Long getAccountBalanceConfId() {
        return accountBalanceConfId;
    }

    public void setAccountBalanceConfId(Long accountBalanceConfId) {
        this.accountBalanceConfId = accountBalanceConfId;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainId) {
        this.chainName = chainId;
    }

    public String getChainAddress() {
        return chainAddress;
    }

    public void setChainAddress(String chainAddress) {
        this.chainAddress = chainAddress == null ? null : chainAddress.trim();
    }

    public Short getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Short monitorType) {
        this.monitorType = monitorType;
    }

    public BigDecimal getBalanceLimit() {
        return balanceLimit;
    }

    public void setBalanceLimit(BigDecimal balanceLimit) {
        this.balanceLimit = balanceLimit;
    }

    public String getReminderEmail() {
        return reminderEmail;
    }

    public void setReminderEmail(String reminderEmail) {
        this.reminderEmail = reminderEmail == null ? null : reminderEmail.trim();
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
}