package com.optum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.optum.entity.Role;
import com.optum.service.RolePermissionMappingService;

@RestController
@RequestMapping("/roles")
public class RolePermissionMappingController {
	 @Autowired
	    private RolePermissionMappingService rolePermissionService;

	    @PostMapping("/permissions/{roleId}")
	    public ResponseEntity<Role> mapPermissionsToRole(@PathVariable int roleId, @RequestBody List<Integer> permissionIds) {
	        Role updatedRole = rolePermissionService.mapPermissionsToRole(roleId, permissionIds);
	        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
	    }
}
