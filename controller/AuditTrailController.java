package com.optum.controller;

import com.optum.entity.AuditTrail;
import com.optum.service.AuditTrailService;
import com.optum.entity.ResponseWrapper;
import com.optum.dao.ReqRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-trail")
public class AuditTrailController {

    @Autowired
    private AuditTrailService auditTrailService;

    @GetMapping("/logs")
    public ResponseEntity<ResponseWrapper<Page<AuditTrail>>> getAuditTrails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditTrail> auditTrailPage = auditTrailService.getAuditTrails(pageable);
            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), null, "Audit trails retrieved successfully");
            ResponseWrapper<Page<AuditTrail>> responseWrapper = new ResponseWrapper<>(auditTrailPage, reqRes);
            return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while retrieving audit trails");
            return new ResponseEntity<>(new ResponseWrapper<>(null, reqRes), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
