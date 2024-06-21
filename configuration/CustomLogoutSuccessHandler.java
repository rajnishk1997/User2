package com.optum.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.optum.service.AuditTrailService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);
    private final AuditTrailService auditTrailService;

    public CustomLogoutSuccessHandler(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        try {
            Integer currentUserId = (Integer) request.getAttribute("currentUserId"); // assuming you set it earlier
            logger.debug("Logout request received for user ID: {}", currentUserId);
            if (authentication != null) {
                logger.debug("Authentication object is not null, proceeding with logout");
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            } else {
                logger.warn("Authentication object is null, cannot perform logout");
            }
            // Log the logout action
            auditTrailService.logAuditTrailWithUsername("logout", "SUCCESS", "Logged out successfully", currentUserId);
            logger.info("User with ID {} logged out successfully", currentUserId);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error("Logout failed for user with ID: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Log Out Action performed in {}ms", duration);
        }
    }
}
