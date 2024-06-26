package com.optum.dto;

public class SotFieldDetailsDto {

    private String sotFieldName;
    private String sotFieldRename;
    private boolean isSOTValidationRequired;
    private Integer currentUserId;

    // Getters and setters
    public String getSotFieldName() {
        return sotFieldName;
    }

    public void setSotFieldName(String sotFieldName) {
        this.sotFieldName = sotFieldName;
    }

    public String getSotFieldRename() {
        return sotFieldRename;
    }

    public void setSotFieldRename(String sotFieldRename) {
        this.sotFieldRename = sotFieldRename;
    }

    public boolean isSOTValidationRequired() {
        return isSOTValidationRequired;
    }

    public void setSOTValidationRequired(boolean SOTValidationRequired) {
        isSOTValidationRequired = SOTValidationRequired;
    }

	public Integer getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}
}
