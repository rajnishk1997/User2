package com.optum.dto.response;

import java.util.HashSet;
import java.util.Set;

public class RoleInfo {
    private Integer roleRid;
    private String roleName;
    private Set<PermissionInfo> permissions;
	
	public Integer getRoleRid() {
		return roleRid;
	}
	public void setRoleRid(Integer roleRid) {
		this.roleRid = roleRid;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Set<PermissionInfo> getPermissions() {
		return permissions;
	}
	public void setPermissions(Set<PermissionInfo> permissions) {
		this.permissions = permissions;
	}

	 public RoleInfo(int roleRid, String roleName) {
	        this.roleRid = roleRid;
	        this.roleName = roleName;
	        this.permissions = new HashSet<>();
	    }
}