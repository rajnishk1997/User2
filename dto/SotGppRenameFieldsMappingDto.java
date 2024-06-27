package com.optum.dto;

public class SotGppRenameFieldsMappingDto {

    private String sotFieldRename;
    private String gppFieldRename;
    private String sotGppRemark;
    private Integer currentUserId;

    // Getters and Setters
    public String getSotFieldRename() {
        return sotFieldRename;
    }

    public void setSotFieldRename(String sotFieldRename) {
        this.sotFieldRename = sotFieldRename;
    }

    public String getGppFieldRename() {
        return gppFieldRename;
    }

    public void setGppFieldRename(String gppFieldRename) {
        this.gppFieldRename = gppFieldRename;
    }

    public String getSotGppRemark() {
        return sotGppRemark;
    }

    public void setSotGppRemark(String sotGppRemark) {
        this.sotGppRemark = sotGppRemark;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Integer currentUserId) {
        this.currentUserId = currentUserId;
    }
}
