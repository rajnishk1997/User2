package com.optum.dto;

import java.util.List;
import java.util.Set;

public class RoleDTO {
    private int roleRid;
    private String roleName;
    private List<PermissionDTO> permissions;
	
    
	public RoleDTO(int roleRid2, String roleName2) {
		// TODO Auto-generated constructor stub
		this.roleRid = roleRid2;
		this.roleName = roleName2;
	}
	public RoleDTO() {
		// TODO Auto-generated constructor stub
	}
	public int getRoleRid() {
		return roleRid;
	}
	public void setRoleRid(int roleRid) {
		this.roleRid = roleRid;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public List<PermissionDTO> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<PermissionDTO> permissions) {
		this.permissions = permissions;
	}
	
//	public Set<PermissionDTO> getPermissions() {
//		return permissions;
//	}
//	public void setPermissions(Set<PermissionDTO> permissions) {
//		this.permissions = permissions;
//	}

   
}
