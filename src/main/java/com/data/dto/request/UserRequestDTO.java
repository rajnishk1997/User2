package com.optum.dto.request;

import java.util.List;
import java.util.Set;

import com.optum.dto.RoleDTO;

public class UserRequestDTO {
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userEmployeeId;
    private Set<RoleDTO> roles;
    private Integer currentUserId;
    
	public String getUserFirstName() {
		return userFirstName;
	}
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	
	
	public Integer getCurrentUserId() {
		return currentUserId;
	}
	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
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
	public String getUserEmployeeId() {
		return userEmployeeId;
	}
	public void setUserEmployeeId(String userEmployeeId) {
		this.userEmployeeId = userEmployeeId;
	}
	public Set<RoleDTO> getRoles() {
		return roles;
	}
	public void setRoles(Set<RoleDTO> roles) {
		this.roles = roles;
	}
	
//	public List<RoleDTO> getRoles() {
//		return roles;
//	}
//	public void setRoles(List<RoleDTO> roles) {
//		this.roles = roles;
//	}
	

    
}
