package com.optum.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optum.dto.UserInfo;
import com.optum.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer>, CustomUserRepository {
	Optional<User> findByUserName(String userName);

	boolean existsByUserEmail(String userEmail);

	Optional<User> findByUserRid(Integer userId);

	 @Query("SELECT new com.optum.dto.UserInfo(u.userRid, u.userName, u.userFirstName, u.userLastName, u.userEmail, u.isActiveUser) " +
	           "FROM User u WHERE u.isNewUser = true")
	    List<UserInfo> findNewUsers();

	User findByUserNameAndIsNewUserTrue(String userName);

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
}
