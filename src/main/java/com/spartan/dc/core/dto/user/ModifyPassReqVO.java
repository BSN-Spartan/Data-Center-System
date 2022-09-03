package com.spartan.dc.core.dto.user;

/**
 * @Author : rjx
 * @Date : 2022/7/26 11:22
 **/
public class ModifyPassReqVO {

    private String oldPassword;

    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
