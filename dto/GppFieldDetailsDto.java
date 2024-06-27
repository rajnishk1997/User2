package com.optum.dto;

public class GppFieldDetailsDto {

    private String gppFieldName;
    private String gppFieldRename;
    private boolean isGPPValidationRequired;
    private Integer currentUserId;

    // Getters and Setters
    public String getGppFieldName() {
        return gppFieldName;
    }

    public void setGppFieldName(String gppFieldName) {
        this.gppFieldName = gppFieldName;
    }

    public String getGppFieldRename() {
        return gppFieldRename;
    }

    public void setGppFieldRename(String gppFieldRename) {
        this.gppFieldRename = gppFieldRename;
    }

    public boolean isGPPValidationRequired() {
        return isGPPValidationRequired;
    }

    public void setGPPValidationRequired(boolean GPPValidationRequired) {
        isGPPValidationRequired = GPPValidationRequired;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Integer currentUserId) {
        this.currentUserId = currentUserId;
    }
}
