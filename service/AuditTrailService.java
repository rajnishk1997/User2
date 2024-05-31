package com.optum.service;

import com.optum.entity.AuditTrail;
import com.optum.dao.AuditTrailRepository;
import com.optum.dao.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditTrailService {
	
	@Autowired
	private UserDao userDao;

    @Autowired
    private AuditTrailRepository auditTrailRepository;

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


}
