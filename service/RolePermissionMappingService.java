package com.optum.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optum.dao.PermissionDao;
import com.optum.dao.RoleDao;
import com.optum.entity.Permission;
import com.optum.entity.Role;

@Service
public class RolePermissionMappingService {

	 @Autowired
	    private RoleDao roleRepository;

	    @Autowired
	    private PermissionDao permissionRepository;

	    @Transactional
	    public Role mapPermissionsToRole(int roleId, List<Integer> permissionIds) {
	        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
	        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));

	        role.setPermissions(permissions);
	        return roleRepository.save(role);
	    }
}
