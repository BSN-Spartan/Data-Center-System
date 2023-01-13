package com.spartan.dc.core.util.user;

import com.spartan.dc.model.SysResource;

import java.util.List;

/**
 * Desc：User Login Info
 *
 * @Created by 2020-02-21 11:09
 */
public class UserLoginInfo {

    private String userName;

    private String systemName;

    private String systemIcon;

    private String systemLogo;

    private Long userId;

    private String email;

    private int dataCenterType;

    private String successUrl = "/index";

    private List<SysResource> resourceList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemIcon() {
        return systemIcon;
    }

    public void setSystemIcon(String systemIcon) {
        this.systemIcon = systemIcon;
    }

    public String getSystemLogo() {
        return systemLogo;
    }

    public void setSystemLogo(String systemLogo) {
        this.systemLogo = systemLogo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public int getDataCenterType() {
        return dataCenterType;
    }

    public void setDataCenterType(int dataCenterType) {
        this.dataCenterType = dataCenterType;
    }

    public List<SysResource> getResourceList() {
        return this.resourceList;
    }

    public void setResourceList(final List<SysResource> resourceList) {
        this.resourceList = resourceList;
    }
}
