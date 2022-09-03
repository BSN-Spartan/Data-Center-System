package com.spartan.dc.core.dto.user;

/**
 * @Author : rjx
 * @Date : 2022/7/27 11:32
 **/
public class UpdateUserStateReqVO {
    private Long userId;

    private String userState;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }
}
