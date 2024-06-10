package com.optum.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.optum.dto.request.SOTNetworkMasterRequestDTO;
import com.optum.entity.SOTNetworkMaster;
import com.optum.entity.SPlatform;
import com.optum.service.AuditTrailService;
import com.optum.service.SOTNewtorkMasterService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SOTNetworkMasterController {
	
	private static final Logger logger = LogManager.getLogger(SOTNetworkMasterController.class);

	@Autowired
	SOTNewtorkMasterService sotNetworkMasterService;
	
	@Autowired
    private AuditTrailService auditTrailService;
	
    @PostConstruct
    public void initPlatforms() {
        try {
        	sotNetworkMasterService.addPlatformNames();
            System.out.println("Platform names initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred while initializing platform names: " + e.getMessage());
        }
    }
    
    @PostMapping("/saveNetworkInfo")
    public ResponseEntity<String> saveNetworkInfo(@RequestBody SOTNetworkMasterRequestDTO sotNetworkMasterRequestDTO) {
        long startTime = System.currentTimeMillis();
        int currentUserRid = sotNetworkMasterRequestDTO.getCurrentUserId();
        
        try {
          
            
            // Saving network information
        	 SOTNetworkMaster savedNetworkMaster =  sotNetworkMasterService.saveNetworkInfo(sotNetworkMasterRequestDTO);

            // Log audit trail asynchronously
            CompletableFuture.runAsync(() -> {
                String details = String.format(
                    "SOT Network Name: %s, GPP Network Name: %s, Platform: %s",
                    savedNetworkMaster.getsSotNetworkName(),
                    savedNetworkMaster.getsGppNetworkName(),
                    savedNetworkMaster.getsPlatform().getsPlatformName()
                );
                auditTrailService.logAuditTrailWithUsername("Network Info Saved", "SUCCESS", details, currentUserRid);
            });

            // Returning success response
            return ResponseEntity.status(HttpStatus.CREATED).body("Network information saved successfully.");
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save network information.");
        } finally {
            // Log action duration
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Save Network Info Action performed in " + duration + "ms");
        }
    }

    @PutMapping("/updateNetworkInfo")
    public ResponseEntity<String> updateNetworkInfo(@RequestBody SOTNetworkMasterRequestDTO sotNetworkMasterRequestDTO) {
        long startTime = System.currentTimeMillis();

        try {
            // Updating network information
            sotNetworkMasterService.updateNetworkInfo(sotNetworkMasterRequestDTO);

            // Returning success response
            return ResponseEntity.status(HttpStatus.OK).body("Network information updated successfully.");
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update network information.");
        } finally {
            // Log action duration
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Update Network Info Action performed in " + duration + "ms");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchNetworkInfo(@RequestParam(required = false) String sotNetworkName,
                                               @RequestParam(required = false) String gppNetworkName,
                                               @RequestParam(required = false) String platformName) {
        try {
            List<SOTNetworkMaster> results = sotNetworkMasterService.searchNetworkInfo(sotNetworkName, gppNetworkName, platformName);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to search network information");
        }
    }
    
    @GetMapping("getSOTNetwork/{sRid}")
    public ResponseEntity<?> getNetworkInfoBySRid(@PathVariable int sRid) {
        try {
            SOTNetworkMaster networkInfo = sotNetworkMasterService.getNetworkInfoBySRid(sRid);
            return ResponseEntity.ok(networkInfo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get network information");
        }
    }

    @GetMapping("/allSOTNetworkMaster")
    public ResponseEntity<?> getAllNetworkInfo() {
        try {
            List<SOTNetworkMaster> allNetworkInfo = sotNetworkMasterService.getAllNetworkInfo();
            return ResponseEntity.ok(allNetworkInfo);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get all network information");
        }
    }
}
