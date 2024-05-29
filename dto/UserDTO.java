package com.optum.dto;

import java.util.List;

public class UserDTO {
	private String userName;
    private String userFirstName;
    private String userMiddleName;
    private String userLastName;
    private String userPassword;
    private String userMobile;
    private String userEmail;
    private String userEmployeeId;
    private List<RoleDTO> roleName;
    private String userDesignation;
    private int userRid;
    
	public int getUserRid() {
		return userRid;
	}
	public void setUserRid(int userRid) {
		this.userRid = userRid;
	}
	public String getUserFirstName() {
		return userFirstName;
	}
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	public String getUserMiddleName() {
		return userMiddleName;
	}
	public void setUserMiddleName(String userMiddleName) {
		this.userMiddleName = userMiddleName;
	}
	public String getUserLastName() {
		return userLastName;
	}
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserMobile() {
		return userMobile;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserEmployeeId() {
		return userEmployeeId;
	}
	public void setUserEmployeeId(String userEmployeeId) {
		this.userEmployeeId = userEmployeeId;
	}
	
	public List<RoleDTO> getRoleName() {
		return roleName;
	}
	public void setRoleName(List<RoleDTO> roleName) {
		this.roleName = roleName;
	}
	public String getUserDesignation() {
		return userDesignation;
	}
	public void setUserDesignation(String userDesignation) {
		this.userDesignation = userDesignation;
	}

	public void setRoles(List<RoleDTO> roles) {
        this.roleName = roles;
    }
}
