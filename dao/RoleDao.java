package com.optum.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optum.entity.Role;

@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {

	//Role findByRoleName(String string);
	 Optional<Role> findByRoleName(String roleName);
	 //Role findByName(String roleName);

	Optional<Role> findByRoleRid(int id);

	 List<Role> findByRoleNameIn(Set<String> roleNames);
}
