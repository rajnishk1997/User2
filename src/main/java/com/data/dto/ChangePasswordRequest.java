package com.optum.dto;

public class ChangePasswordRequest {
    private String userName;
    private String userPassword;

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
}

