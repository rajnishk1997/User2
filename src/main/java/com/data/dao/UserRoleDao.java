package com.optum.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optum.entity.Role;
import com.optum.entity.User;
import com.optum.entity.UserRole;

@Repository
public interface UserRoleDao extends JpaRepository<UserRole, Integer> {

	 Optional<UserRole> findByUserAndRole(User user, Role role);

	List<UserRole> findByUser(User user);

	//Set<Role> findRolesByUserRid(int userRid);

}
