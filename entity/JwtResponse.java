package com.optum.entity;

import java.util.HashSet;
import java.util.Set;

import com.optum.dto.response.RoleInfo;

public class JwtResponse {

	// private User user;
	private int statusCode;
	private String error;
	private String message;
	private boolean firstLogin;
	private Integer currentUserId;
	private String jwtToken;
	private Set<RoleInfo> roles;
	private String userName;
	private String userEmail;

//    public JwtResponse(int statusCode, String error, String message, String jwtToken, User user) {
//        this.statusCode = statusCode;
//        this.error = error;
//        this.message = message;
//        this.jwtToken = jwtToken;
//        this.userName = user.getUserName();
//        this.userEmail = user.getUserEmail();
//        this.currentUserRid=user.getUserRid();
//    }

	public int getStatusCode() {
		return statusCode;
	}

	public Set<RoleInfo> getRoleNames() {
		return roles;
	}

	public void setRoleNames(Set<RoleInfo> roleNames) {
		this.roles = roleNames;
	}

//	public JwtResponse(int statusCode, String error, String message, String jwtToken, Set<RoleInfo> roleInfos,
//			User user) {
//		super();
//		this.statusCode = statusCode;
//		this.error = error;
//		this.message = message;
//		this.currentUserId = user.getUserRid();
//		this.jwtToken = jwtToken;
//		this.roleNames = roleInfos;
//		this.userName = user.getUserName();
//		this.userEmail = user.getUserEmail();
//		this.firstLogin = user.isFirstLogin();
//
//	}
	public JwtResponse(int statusCode, String message, String error, String jwtToken, Set<RoleInfo> roleInfos, User user) {
	    this.statusCode = statusCode;
	    this.message = message != null ? message : "";
	    this.error = error != null ? error : "";
	    this.jwtToken = jwtToken != null ? jwtToken : "";
	    this.roles = roleInfos != null ? roleInfos : new HashSet<>();
	    
	    if (user != null) {
	        this.userName = user.getUserName() != null ? user.getUserName() : "";
	        this.userEmail = user.getUserEmail() != null ? user.getUserEmail() : "";
	        this.firstLogin = user.isFirstLogin();
	    } else {
	        this.userName = "";
	        this.userEmail = "";
	    }
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getError() {
		return error;
	}

	public Integer getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Integer currentUserRid) {
		this.currentUserId = currentUserRid;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}
}
