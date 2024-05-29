package com.optum.dto;

public class PermissionDTO {
    private int permissionRid;
    private String permissionName;
	
	public int getPermissionRid() {
		return permissionRid;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public void setPermissionRid(int permissionRid) {
        this.permissionRid = permissionRid;
    }
}

