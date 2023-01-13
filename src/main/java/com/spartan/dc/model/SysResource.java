package com.spartan.dc.model;

public class SysResource {
    private Long rsucId;

    private Long parentId;

    private String rsucCode;

    private String rsucName;

    private String rsucUrl;

    private Integer priority;

    private Short rsucType;

    private Short state;

    public Long getRsucId() {
        return rsucId;
    }

    public void setRsucId(Long rsucId) {
        this.rsucId = rsucId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getRsucCode() {
        return rsucCode;
    }

    public void setRsucCode(String rsucCode) {
        this.rsucCode = rsucCode == null ? null : rsucCode.trim();
    }

    public String getRsucName() {
        return rsucName;
    }

    public void setRsucName(String rsucName) {
        this.rsucName = rsucName == null ? null : rsucName.trim();
    }

    public String getRsucUrl() {
        return rsucUrl;
    }

    public void setRsucUrl(String rsucUrl) {
        this.rsucUrl = rsucUrl == null ? null : rsucUrl.trim();
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Short getRsucType() {
        return rsucType;
    }

    public void setRsucType(Short rsucType) {
        this.rsucType = rsucType;
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }
}