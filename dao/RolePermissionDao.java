package com.optum.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optum.entity.Role;
import com.optum.entity.RolePermission;

@Repository
public interface RolePermissionDao extends JpaRepository<RolePermission, Integer>  {

	//Set<RolePermission> findByRoleId(int roleRid);
	 Set<RolePermission> findByRoleRoleRid(int roleRid);

	List<RolePermission> findByRoleRoleName(String roleName);
	
	 List<RolePermission> findByRole(Role role);
	

}
