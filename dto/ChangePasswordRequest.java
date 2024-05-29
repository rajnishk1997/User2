package com.optum.dto;

public class ChangePasswordRequest {
    private String userName;
    private String userPassword;
    private Integer currentUserId;

    // Getters and setters
    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getNewPassword() {
        return userPassword;
    }

    public void setNewPassword(String newPassword) {
        this.userPassword = newPassword;
    }

	public Integer getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}
    
    
    
    
}

