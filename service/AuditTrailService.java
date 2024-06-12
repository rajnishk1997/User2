package com.optum.service;

import com.optum.entity.AuditTrail;
import com.optum.entity.User;
import com.optum.dao.AuditTrailRepository;
import com.optum.dao.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

@Service
public class AuditTrailService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuditTrailRepository auditTrailRepository;
	
	@Autowired
	private UserDao userRepository;

	@Async
	public void logAuditTrail(String action, String status, String details, int userId, Date timestamp) {
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setAction(action);
		auditTrail.setStatus(status);
		auditTrail.setDetails(details);
		auditTrail.setTimestamp(timestamp);
		auditTrail.setUserId(userId);
		auditTrailRepository.save(auditTrail);
	}

	@Async
	public void logAuditTrailWithUsername(String action, String status, String details, int userId) {
		String currentUserUsername = userDao.findUserNameByUserRid(userId);
		String detailedMessage = details + " by " + currentUserUsername;
		logAuditTrail(action, status, detailedMessage, userId, new Date());
	}

	public Page<AuditTrail> getAuditTrails(Pageable pageable) {
		return auditTrailRepository.findAllByOrderByTimestampDesc(pageable);
	}

	public List<AuditTrail> filterAuditTrail(String userName, String action, Date fromDate, Date toDate) {
	    Specification<AuditTrail> spec = Specification.where(null);

	    if (userName != null) {
	        spec = spec.and((root, query, criteriaBuilder) ->
	                criteriaBuilder.equal(root.get("userId"), getUserIdFromUserName(userName)));
	    }

	    if (action != null) {
	        spec = spec.and((root, query, criteriaBuilder) ->
	                criteriaBuilder.equal(root.get("action"), action));
	    }

	    if (fromDate != null && toDate != null) {
	        spec = spec.and((root, query, criteriaBuilder) ->
	                criteriaBuilder.between(root.get("timestamp"), fromDate, toDate));
	    } else if (fromDate != null) {
	        spec = spec.and((root, query, criteriaBuilder) ->
	                criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), fromDate));
	    } else if (toDate != null) {
	        spec = spec.and((root, query, criteriaBuilder) ->
	                criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), toDate));
	    }

	    Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("timestamp").descending());

	    Page<AuditTrail> pageResult = auditTrailRepository.findAll(spec, pageable); //execute the query 

	    return pageResult.getContent();
	}

	 private Integer getUserIdFromUserName(String userName) {
	        // Query the database to find the user by username
	        User user = userRepository.findByUserName(userName)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	        // Return the user ID
	        return user.getUserRid();
	    }

}
