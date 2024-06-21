package com.optum.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.optum.controller.UserController;
import com.optum.service.AuditTrailService;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	
	private static final Logger logger = LogManager.getLogger(CustomLogoutSuccessHandler.class);

    private final AuditTrailService auditTrailService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CustomLogoutSuccessHandler(AuditTrailService auditTrailService, ObjectMapper objectMapper) {
        this.auditTrailService = auditTrailService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        long startTime = System.currentTimeMillis();
        Integer currentUserId = null;
        try {
            // Read request body
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String requestBody = reader.lines().reduce("", (accumulator, actual) -> accumulator + actual);

            // Parse JSON
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            currentUserId = jsonNode.get("currentUserId").asInt();

            // Perform logout
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            }

            // Log the logout action
            auditTrailService.logAuditTrailWithUsername("logout", "SUCCESS", "Logged out successfully", currentUserId);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
        	 long endTime = System.currentTimeMillis();
 	        long duration = endTime - startTime;
 	        logger.info("Logout Action performed in " + duration + "ms");
        }
    }
}
