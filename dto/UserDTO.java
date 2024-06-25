package com.optum.dto;

import java.util.List;

public class UserDTO {
	private String userName;
    private String userFirstName;
    private String userLastName;
    private String userPassword;
    private String userEmail;
    private List<RoleDTO> roleName;
    private int userRid;
    private Integer managerId;
    
 // Constructor
    public UserDTO(String userName, String userFirstName, String userLastName, String userPassword, String userEmail, int userRid,Integer managerId) {
        this.userName = userName;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userRid = userRid;
        this.managerId = managerId;
    }
    
	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public UserDTO() {
		// TODO Auto-generated constructor stub
	}
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
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public List<RoleDTO> getRoleName() {
		return roleName;
	}
	public void setRoleName(List<RoleDTO> roleName) {
		this.roleName = roleName;
	}

	public void setRoles(List<RoleDTO> roles) {
        this.roleName = roles;
    }
}
