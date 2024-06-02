package com.optum.controller;

import java.sql.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.optum.dao.ReqRes;
import com.optum.entity.JwtRequest;
import com.optum.entity.JwtResponse;
import com.optum.service.AuditTrailService;
import com.optum.service.JwtService;
import com.optum.service.UserService;

@RestController
@CrossOrigin
public class JwtController {
	
	private static final Logger logger = LogManager.getLogger(JwtController.class);

    @Autowired
    private JwtService jwtService;
    
	@Autowired
    private AuditTrailService auditTrailService;

	@PostMapping("/login")
    public ResponseEntity<JwtResponse> createJwtToken(@RequestBody JwtRequest jwtRequest) {
        long startTime = System.currentTimeMillis();
        Integer currentUserRid = null;
        try {
            JwtResponse customJwtResponse = jwtService.createJwtToken(jwtRequest);
            currentUserRid = customJwtResponse.getCurrentUserId();
            auditTrailService.logAuditTrailWithUsername("createJwtToken", "SUCCESS", "Logged In successfully for username: " + jwtRequest.getUserName(), currentUserRid);
            return ResponseEntity.ok(customJwtResponse);
        } catch (BadCredentialsException e) {
           
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JwtResponse(401, "Unauthorized", "Invalid Credentials", null, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JwtResponse(500, "Internal Server Error", "Something went wrong", null, null, null));
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Action performed in " + duration + "ms");
        }
    }
}
