package com.optum.dto;

public class UserInfo {

	private String userName;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private boolean isActiveUser;
    private int userRid;
    
    public UserInfo(int userRid, String userName, String userFirstName, String userLastName, String userEmail, boolean isActiveUser) {
        this.userRid = userRid;
        this.userName = userName;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.isActiveUser = isActiveUser;
    }
    
	public int getUserRid() {
		return userRid;
	}
	public void setUserRid(int userRid) {
		this.userRid = userRid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserFirstName() {
		return userFirstName;
	}
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	public String getUserLastName() {
		return userLastName;
	}
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public boolean isActiveUser() {
		return isActiveUser;
	}
	public void setActiveUser(boolean isActiveUser) {
		this.isActiveUser = isActiveUser;
	}
    
    
    
}
