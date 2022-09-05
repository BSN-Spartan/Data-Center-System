package com.spartan.dc.core.dto.user;

import javax.validation.constraints.NotEmpty;

/**
 * @Author : rjx
 * @Date : 2022/7/26 11:22
 **/
public class AddUserReqVO {

    @NotEmpty(message = "userName can not be empty")
    private String userName;

    @NotEmpty(message = "password can not be empty")
    private String password;

    @NotEmpty(message = "email can not be empty")
    private String email;

    private String phone;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
