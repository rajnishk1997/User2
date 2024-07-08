package com.optum.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.optum.dao.ReqRes;
import com.optum.dto.SotFieldDetailsDto;
import com.optum.dto.SotRenameDto;
import com.optum.entity.GppFieldDetails;
import com.optum.entity.RegistrationResponse;
import com.optum.entity.ResponseWrapper;
import com.optum.entity.SotFieldDetails;
import com.optum.service.AuditTrailService;
import com.optum.service.SotFieldDetailsService;

@RestController
@RequestMapping("/api/sotFieldDetails")
public class SotFieldDetailsController {
	
	private static final Logger logger = LogManager.getLogger(SotFieldDetailsController.class);

    @Autowired
    private SotFieldDetailsService sotFieldDetailsService;
    
    @Autowired
    private AuditTrailService auditTrailService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<SotFieldDetails>> createSotFieldDetails(@RequestBody SotFieldDetailsDto sotFieldDetailsDto) {
    	Integer currentUserRid = sotFieldDetailsDto.getCurrentUserId();
        long startTime = System.currentTimeMillis();
        try {
            SotFieldDetails createdSotFieldDetails = sotFieldDetailsService.createSotFieldDetails(sotFieldDetailsDto, currentUserRid);

            // Log audit trail asynchronously
            CompletableFuture.runAsync(() -> {
                String details = String.format(
                    "SotFieldName: %s, SotFieldRename: %s, SOTValidationRequired: %s",
                    createdSotFieldDetails.getSotFieldName(),
                    createdSotFieldDetails.getSotFieldRename(),
                    createdSotFieldDetails.isSOTValidationRequired()
                );
                auditTrailService.logAuditTrailWithUsername("SOT Field Created", "SUCCESS", details, currentUserRid);
            });

            // Create and return the response
            ReqRes reqRes = new ReqRes(HttpStatus.CREATED.value(), "SUCCESS", "SOT Field created successfully");
            ResponseWrapper<SotFieldDetails> response = new ResponseWrapper<>(createdSotFieldDetails, reqRes);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
            ResponseWrapper<SotFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while creating the SOT Field");
            ResponseWrapper<SotFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }  finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("SOT Field Created Action performed in " + duration + "ms");
        }
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<SotFieldDetails>> updateSotFieldDetails(@PathVariable int id, @RequestBody SotFieldDetailsDto sotFieldDetailsDto) {
    	Integer currentUserRid = sotFieldDetailsDto.getCurrentUserId();
        long startTime = System.currentTimeMillis();
        try {
            SotFieldDetails existingSotFieldDetails = sotFieldDetailsService.getSotFieldDetailsById(id);
            SotFieldDetails updatedSotFieldDetails = sotFieldDetailsService.updateSotFieldDetails(id, sotFieldDetailsDto, currentUserRid);

            // Log audit trail asynchronously
            CompletableFuture.runAsync(() -> {
                String oldDetails = String.format(
                    "Old SotFieldName: %s, Old SotFieldRename: %s, Old SOTValidationRequired: %s",
                    existingSotFieldDetails.getSotFieldName(),
                    existingSotFieldDetails.getSotFieldRename(),
                    existingSotFieldDetails.isSOTValidationRequired()
                );
                String newDetails = String.format(
                    "New SotFieldName: %s, New SotFieldRename: %s, New SOTValidationRequired: %s",
                    updatedSotFieldDetails.getSotFieldName(),
                    updatedSotFieldDetails.getSotFieldRename(),
                    updatedSotFieldDetails.isSOTValidationRequired()
                );
                String details = oldDetails + "; " + newDetails;
                auditTrailService.logAuditTrailWithUsername("SOT Field Updated", "SUCCESS", details, currentUserRid);
            });

            // Create and return the response
            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "SOT Field updated successfully");
            ResponseWrapper<SotFieldDetails> response = new ResponseWrapper<>(updatedSotFieldDetails, reqRes);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            ReqRes reqRes = new ReqRes(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
            ResponseWrapper<SotFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while updating the SOT Field");
            ResponseWrapper<SotFieldDetails> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Update SOT Field Details Action performed in " + duration + "ms");
        }
    }

    
    @GetMapping
    public ResponseEntity<List<SotFieldDetails>> getAllSotFieldDetails() {
        List<SotFieldDetails> sotFieldDetailsList = sotFieldDetailsService.getAllSotFieldDetails();
        return new ResponseEntity<>(sotFieldDetailsList, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SotFieldDetails> getSotFieldDetailsById(@PathVariable int id) {
        SotFieldDetails sotFieldDetails = sotFieldDetailsService.getSotFieldDetailsById(id);
        return new ResponseEntity<>(sotFieldDetails, HttpStatus.OK);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<SotFieldDetails>> searchSotFieldDetails(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean validate) {
        List<SotFieldDetails> sotFieldDetailsList = sotFieldDetailsService.searchSotFieldDetails(keyword, validate);
        return new ResponseEntity<>(sotFieldDetailsList, HttpStatus.OK);
    }
    
    @GetMapping("/getSotRenames")
    public ResponseEntity<ResponseWrapper<List<SotRenameDto>>> getAllSotRenames() {
        long startTime = System.currentTimeMillis();
        try {
            List<SotRenameDto> sotRenames = sotFieldDetailsService.getAllSotRenames();
            ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Fetched all SOT renames successfully");
            ResponseWrapper<List<SotRenameDto>> response = new ResponseWrapper<>(sotRenames, reqRes);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while fetching SOT renames");
            ResponseWrapper<List<SotRenameDto>> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Fetched all SOT renames in " + duration + "ms");
        }
    }
    
    @DeleteMapping("/deleteSotField/{sotRid}")
    public ResponseEntity<ResponseWrapper<String>> deleteSotField(@PathVariable int sotRid) {
        long startTime = System.currentTimeMillis();
        try {
            List<GppFieldDetails> mappedGppFields = sotFieldDetailsService.getMappedGppFieldsBySotRid(sotRid);

            if (!mappedGppFields.isEmpty()) {
                String mappedFields = mappedGppFields.stream()
                                                     .map(GppFieldDetails::getGppFieldRename)
                                                     .collect(Collectors.joining(", "));
                String message = String.format("SotRenameField with ID \"%d\" is being mapped with gppRenameFields \"%s\"", sotRid, mappedFields);
                ReqRes reqRes = new ReqRes(HttpStatus.CONFLICT.value(), "Conflict", message);
                ResponseWrapper<String> response = new ResponseWrapper<>(null, reqRes);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            boolean isDeleted = sotFieldDetailsService.deleteSotFieldById(sotRid);
            if (isDeleted) {
                ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "Deleted SOT field successfully");
                ResponseWrapper<String> response = new ResponseWrapper<>("Deleted successfully", reqRes);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                ReqRes reqRes = new ReqRes(HttpStatus.NOT_FOUND.value(), "Not Found", "SOT field not found");
                ResponseWrapper<String> response = new ResponseWrapper<>(null, reqRes);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            ReqRes reqRes = new ReqRes(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "An error occurred while deleting SOT field");
            ResponseWrapper<String> response = new ResponseWrapper<>(null, reqRes);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("Processed delete SOT field request in " + duration + "ms");
        }
    }
    
}
