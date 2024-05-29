package com.optum.service;

import com.optum.entity.AuditTrail;
import com.optum.dao.AuditTrailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditTrailService {

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    public void logAuditTrail(String action, String status, String details, int userId) {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setAction(action);
        auditTrail.setStatus(status);
        auditTrail.setDetails(details);
        auditTrail.setTimestamp(new Date());
        auditTrail.setUserId(userId);
        auditTrailRepository.save(auditTrail);
    }
}
