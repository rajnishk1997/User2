package com.optum.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optum.entity.*;

@Repository
public interface PermissionDao extends JpaRepository<Permission, Integer> {

	Permission findByPermissionName(String string);
    // You can define custom query methods here if needed

	List<Permission> findAllByPermissionNameIn(List<String> asList);

	List<Permission> findByPermissionNameIn(List<String> permissionNames);
}
