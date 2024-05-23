package com.optum.dto;

public class RoleInfo {
	   private int roleRid;
	    private String roleName;
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
		public RoleInfo(int roleRid, String roleName) {
			super();
			this.roleRid = roleRid;
			this.roleName = roleName;
		}
	    
}
