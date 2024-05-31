package com.optum.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optum.entity.Role;

@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {

	//Role findByRoleName(String string);
	 Optional<Role> findByRoleName(String roleName);
	 //Role findByName(String roleName);

	 @Query("SELECT r FROM Role r WHERE r.roleRid = :id")
	    Optional<Role> findByRoleRid(@Param("id") int id);
	

	 List<Role> findByRoleNameIn(Set<String> roleNames);
}
