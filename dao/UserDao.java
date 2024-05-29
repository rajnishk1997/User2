package com.optum.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optum.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer>, CustomUserRepository  {
	Optional<User> findByUserName(String userName);

	//List<User> findByUserNameContainingIgnoreCaseOrUserFirstNameContainingIgnoreCase(String keyword, String keyword2);

	//List<User> findByUserFirstName(String userFirstName);
	//Optional<User> findByUserEmail(String userEmail);
	//List<User> findByUserEmailContainingIgnoreCaseOrUserFirstNameContainingIgnoreCase(String keyword, String keyword2);

	//List<User> findByUserLastName(String userLastName);

	//boolean existsByUserMobile(String userMobile);

	boolean existsByUserEmail(String userEmail);

	Optional<User> findByUserRid(Integer userId);

	List<User> findByIsNewUserTrue();

	User findByUserNameAndIsNewUserTrue(String userName);

	List<User> findByUserFirstNameContainingIgnoreCaseOrUserMiddleNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrUserNameContainingIgnoreCase(
			String keyword, String keyword2, String keyword3, String keyword4);

	//void deleteUserByUserName(String username);
	
	


}
