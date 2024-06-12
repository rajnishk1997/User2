package com.optum.controller;

import com.optum.entity.AuditTrail;
import com.optum.service.AuditTrailService;
import com.optum.entity.ResponseWrapper;
import com.optum.dao.ReqRes;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/audit-trail")
public class AuditTrailController {

    @Autowired
    private AuditTrailService auditTrailService;
    
    @GetMapping("/logs")
    public ResponseEntity<ResponseWrapper<Page<AuditTrail>>> getAuditTrails(
            @RequestParam(defaultValue = "0") int page) {
        int size = 10; // Fixed page size
        try {
            // Create a pageable object with sorting by timestamp in descending order
            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
            Page<AuditTrail> auditTrailPage = auditTrailService.getAuditTrails(pageable);

            // Create response wrapper
            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), null, "Audit trails retrieved successfully");
            ResponseWrapper<Page<AuditTrail>> responseWrapper = new ResponseWrapper<>(auditTrailPage, reqRes);

            // Return response entity
            return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while retrieving audit trails");
            return new ResponseEntity<>(new ResponseWrapper<>(null, reqRes), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/audit-trail")
    public ResponseEntity<List<AuditTrail>> filterAuditTrail(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

        List<AuditTrail> auditTrailList = auditTrailService.filterAuditTrail(userName, action, fromDate, toDate);
        
        return ResponseEntity.ok(auditTrailList);
    }
}
