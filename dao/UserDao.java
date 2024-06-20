package com.optum.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optum.dto.UserDTO;
import com.optum.dto.UserInfo;
import com.optum.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer>, CustomUserRepository {
	
	 @Query("SELECT u FROM User u WHERE u.userName = :userName")
	Optional<User> findByUserName(String userName);

	boolean existsByUserEmail(String userEmail);

	Optional<User> findByUserRid(Integer userId);
	
	 @Query("SELECT u.userName FROM User u WHERE u.userRid = :userRid")
	    String findUserNameByUserRid(@Param("userRid") int userRid);

	@Query("SELECT new com.optum.dto.UserInfo(u.userRid, u.userName, u.userFirstName, u.userLastName, u.userEmail, u.isActiveUser) "
			+ "FROM User u WHERE u.isNewUser = true")
	List<UserInfo> findNewUsers();

	@Query("SELECT u FROM User u WHERE u.userName = :userName AND u.isNewUser = true")
    Optional<User> findByUserNameAndIsNewUserTrue(@Param("userName") String userName);

	List<User> findByUserFirstNameContainingIgnoreCaseOrUserMiddleNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrUserNameContainingIgnoreCase(
			String keyword, String keyword2, String keyword3, String keyword4);

	@Query("SELECT new com.optum.dto.UserInfo(u.userRid, u.userName, u.userFirstName, u.userLastName, u.userEmail, u.isActiveUser) "
			+ "FROM com.optum.entity.User u")
	List<UserInfo> findAllUsersWithoutRoles();

	@Query("SELECT new com.optum.dto.UserInfo(u.userRid, u.userName, u.userFirstName, u.userLastName, u.userEmail, u.isActiveUser) "
			+ "FROM com.optum.entity.User u WHERE "
			+ "LOWER(u.userFirstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ "LOWER(u.userLastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ "LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<UserInfo> searchUsersByKeywordWithoutRoles(@Param("keyword") String keyword);

	 @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles WHERE u.userName = :username")
	    User findUserDetailsWithRolesByUsername(@Param("username") String username);

	  @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role r LEFT JOIN FETCH r.rolePermissions rp LEFT JOIN FETCH rp.permission WHERE u.userName = :userName")
	    Optional<User> findByUserNameWithRolesAndPermissions(@Param("userName") String userName);

	 @Query("SELECT u FROM User u WHERE u.userEmail = :userEmail")
    Optional<User> findByUserEmail(@Param("userEmail") String userEmail);
	 
	  @Query("SELECT new com.optum.dto.UserInfoDTO(u.userName, u.userFirstName, u.userLastName, u.userEmail, u.isActiveUser, u.userRid) " +
	           "FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = :roleName")
	    List<UserInfo> findUserInfosByRoleName(@Param("roleName") String roleName);

	  @Query("SELECT new com.opyum.UserInfo(u.userRid, u.userName, u.userFirstName, u.userLastName, u.email) " +
           "FROM User u WHERE u.isNewUser = true AND u.manager.userRid = :managerId")
    List<UserInfo> findNewUsersByManagerId(@Param("managerId") int managerId);

	 @Query("SELECT u FROM User u WHERE u.manager.userRid = :managerId AND u.isNewUser = false")
    List<User> findByManagerUserRidAndIsNewUserFalse(@Param("managerId") int managerId);
}
