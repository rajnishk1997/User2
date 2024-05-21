package com.optum.service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.optum.dao.CustomUserRepository;
import com.optum.dao.UserDao;
import com.optum.entity.User;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
	private UserDao userDao;

    @Override
    @Transactional
    public void deleteUserByUserName(String username) {
        // Retrieve the userRid based on the username
    	 Optional<User> optionalUser = userDao.findByUserName(username);
    	    if (optionalUser.isPresent()) {
    	        User user = optionalUser.get();
    	        int userRid = user.getUserRid();

    	        // Delete UserRole entries associated with the userRid
    	        entityManager.createQuery("DELETE FROM UserRole ur WHERE ur.user.userRid = :userRid")
    	            .setParameter("userRid", userRid)
    	            .executeUpdate();

    	        // Delete the User entry
    	        entityManager.createQuery("DELETE FROM User u WHERE u.userName = :username")
    	                     .setParameter("username", username)
    	                     .executeUpdate();
    	    }
    }

}
