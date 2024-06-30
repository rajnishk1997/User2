package com.optum.dto;

public class GppSheetDto {
    private Long gppSheetRid;
    private String gppSheetName;
    private boolean isActive;

    // Getters and Setters
    public Long getGppSheetRid() {
        return gppSheetRid;
    }

    public void setGppSheetRid(Long gppSheetRid) {
        this.gppSheetRid = gppSheetRid;
    }

    public String getGppSheetName() {
        return gppSheetName;
    }

    public void setGppSheetName(String gppSheetName) {
        this.gppSheetName = gppSheetName;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}

