package com.optum.dto.response;

public class PermissionInfo {
    private Integer permissionRid;
    private String permissionName;
    
	 public PermissionInfo(int permissionRid, String permissionName) {
        this.permissionRid = permissionRid;
        this.permissionName = permissionName;
    }
	 
	public Integer getPermissionRid() {
		return permissionRid;
	}
	public void setPermissionRid(Integer permissionRid) {
		this.permissionRid = permissionRid;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

    
}