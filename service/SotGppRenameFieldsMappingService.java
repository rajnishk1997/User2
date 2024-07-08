package com.optum.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optum.dao.GppFieldDetailsDao;
import com.optum.dao.GppSheetDao;
import com.optum.dao.ReqRes;
import com.optum.dao.SotFieldDetailsDao;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dto.SotGppRenameFieldsMappingDto;
import com.optum.entity.GppFieldDetails;
import com.optum.entity.GppSheet;
import com.optum.entity.ResponseWrapper;
import com.optum.entity.SotFieldDetails;
import com.optum.entity.SotGppRenameFieldsMapping;

@Service
public class SotGppRenameFieldsMappingService {
	
	@Autowired
	private GppSheetDao gppSheetRepository;

	@Autowired
	private SotFieldDetailsDao sotFieldDetailsRepository;

	@Autowired
	private GppFieldDetailsDao gppFieldDetailsRepository;

	@Autowired
	private SotGppRenameFieldsMappingDao sotGppRenameFieldsMappingRepository;

	@Autowired
	private AuditTrailService auditTrailService;

	@Transactional
	public ResponseWrapper<SotGppRenameFieldsMapping> saveSotGppMapping(SotGppRenameFieldsMappingDto createDto) {
        // Find related entities
        SotFieldDetails sotFieldDetails = sotFieldDetailsRepository.findById(createDto.getSotRid())
                .orElseThrow(() -> new IllegalArgumentException("SOT Field not found."));
        GppFieldDetails gppFieldDetails = gppFieldDetailsRepository.findById(createDto.getGppRid())
                .orElseThrow(() -> new IllegalArgumentException("GPP Field not found."));
        GppSheet gppSheet = gppSheetRepository.findById(createDto.getGppsheetRid())
                .orElseThrow(() -> new IllegalArgumentException("GPP Sheet not found."));

        // Check for existing mapping
        SotGppRenameFieldsMapping existingMapping = sotGppRenameFieldsMappingRepository
                .findBySotFieldDetailsAndGppFieldDetailsAndGppSheet(sotFieldDetails, gppFieldDetails, gppSheet);

        if (existingMapping != null) {
            throw new IllegalArgumentException("Mapping already exists for the provided SOT Field, GPP Field, and GPP Sheet.");
        }

        // Create new mapping
        SotGppRenameFieldsMapping newMapping = new SotGppRenameFieldsMapping();
        newMapping.setSotFieldDetails(sotFieldDetails);
        newMapping.setGppFieldDetails(gppFieldDetails);
        newMapping.setGppSheet(gppSheet);
        newMapping.setSotGppRemark(createDto.getSotGppRemark());
        newMapping.setCreatedBy(createDto.getCurrentUserId());
        newMapping.setCreatedDate(new Date());

        SotGppRenameFieldsMapping savedMapping = sotGppRenameFieldsMappingRepository.save(newMapping);
		 logAuditTrail(savedMapping, null, null, null, null, createDto.getCurrentUserId(), true);

        // Return response wrapper
        ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "SOT-GPP Mapping created successfully");
        return new ResponseWrapper<>(savedMapping, reqRes);
    }

	@Transactional
	public ResponseWrapper<SotGppRenameFieldsMapping> updateSotGppMapping(int sotGppRid,
	        SotGppRenameFieldsMappingDto updateDto) {
	    SotGppRenameFieldsMapping existingMapping = sotGppRenameFieldsMappingRepository.findById(sotGppRid)
	            .orElseThrow(() -> new IllegalArgumentException("SOT-GPP Mapping not found."));

	    // Store old values for audit trail
	    SotFieldDetails oldSotFieldDetails = existingMapping.getSotFieldDetails();
	    GppFieldDetails oldGppFieldDetails = existingMapping.getGppFieldDetails();
	    String oldRemark = existingMapping.getSotGppRemark();
	    GppSheet oldGppSheet = existingMapping.getGppSheet(); 
	    // Check if new sotRid and gppRid are already in use
	    SotFieldDetails sotFieldDetails = sotFieldDetailsRepository.findById(updateDto.getSotRid())
	            .orElseThrow(() -> new IllegalArgumentException("SOT Field not found."));
	    GppFieldDetails gppFieldDetails = gppFieldDetailsRepository.findById(updateDto.getGppRid())
	            .orElseThrow(() -> new IllegalArgumentException("GPP Field not found."));

	    // Check if the new gppsheetRid exists and fetch the corresponding GppSheet entity
	    GppSheet newGppSheet = gppSheetRepository.findById(updateDto.getGppsheetRid())
	            .orElseThrow(() -> new IllegalArgumentException("GPP Sheet not found."));

	    // Check if the new sotRid is already mapped to another GPP Field
	    SotGppRenameFieldsMapping existingSotMapping = sotGppRenameFieldsMappingRepository
	            .findBySotFieldDetails(sotFieldDetails);
	    if (existingSotMapping != null && existingSotMapping.getSotGppRid() != sotGppRid) {
	        throw new IllegalArgumentException("SOT Field is already mapped to another GPP Field.");
	    }

	    // Check if the new gppRid is already mapped to another SOT Field
	    SotGppRenameFieldsMapping existingGppMapping = sotGppRenameFieldsMappingRepository
	            .findByGppFieldDetails(gppFieldDetails);
	    if (existingGppMapping != null && existingGppMapping.getSotGppRid() != sotGppRid) {
	        throw new IllegalArgumentException("GPP Field is already mapped to another SOT Field.");
	    }

	    // Update existing mapping with new values
	    existingMapping.setSotFieldDetails(sotFieldDetails);
	    existingMapping.setGppFieldDetails(gppFieldDetails);
	    existingMapping.setGppSheet(newGppSheet); // Set new GppSheet entity
	    existingMapping.setSotGppRemark(updateDto.getSotGppRemark());
	    existingMapping.setModifiedBy(updateDto.getCurrentUserId());
	    existingMapping.setModifiedDate(new Date());

	    // Save updated mapping
	    SotGppRenameFieldsMapping updatedMapping = sotGppRenameFieldsMappingRepository.save(existingMapping);

	    // Log audit trail with old and new values
	    logAuditTrail(updatedMapping, oldSotFieldDetails, oldGppFieldDetails, oldRemark, oldGppSheet,
	            updateDto.getCurrentUserId(), false);

	    // Return response wrapper
	    ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "SOT-GPP Mapping updated successfully");
	    return new ResponseWrapper<>(updatedMapping, reqRes);
	}



	private void logAuditTrail(SotGppRenameFieldsMapping newMapping, SotFieldDetails oldSotFieldDetails,
	        GppFieldDetails oldGppFieldDetails, String oldRemark, GppSheet oldSheet, Integer currentUserRid,
	        boolean isCreation) {
	    CompletableFuture.runAsync(() -> {
	        String action = isCreation ? "SOT-GPP Mapping Created" : "SOT-GPP Mapping Updated";
	        String status = isCreation ? "CREATED" : "UPDATED";

	        String details;
	        if (isCreation) {
	            details = String.format("SOT Field Rename: %s, GPP Field Rename: %s, SOT-GPP Remark: %s",
	                    newMapping.getSotFieldDetails().getSotFieldRename(),
	                    newMapping.getGppFieldDetails().getGppFieldRename(), newMapping.getSotGppRemark());
	        } else {
	            String oldDetails = String.format(
	                    "Old SOT Field Rename: %s, Old GPP Field Rename: %s, Old SOT-GPP Remark: %s, Old GPP Sheet: %s",
	                    oldSotFieldDetails.getSotFieldRename(), oldGppFieldDetails.getGppFieldRename(), oldRemark,
	                    oldSheet != null ? oldSheet.getGppSheetName() : null); // Accessing GppSheet properties if not null
	            String newDetails = String.format(
	                    "New SOT Field Rename: %s, New GPP Field Rename: %s, New SOT-GPP Remark: %s, New GPP Sheet: %s",
	                    newMapping.getSotFieldDetails().getSotFieldRename(),
	                    newMapping.getGppFieldDetails().getGppFieldRename(), newMapping.getSotGppRemark(),
	                    newMapping.getGppSheet() != null ? newMapping.getGppSheet().getGppSheetName() : null); // Accessing GppSheet properties if not null
	            details = oldDetails + "; " + newDetails;
	        }

	        auditTrailService.logAuditTrailWithUsername(action, status, details, currentUserRid);
	    });
	}


	public List<SotGppRenameFieldsMapping> searchMappings(String sotRename, String gppRename) {
	    if ((sotRename == null || sotRename.isEmpty()) && (gppRename == null || gppRename.isEmpty())) {
	        return sotGppRenameFieldsMappingRepository.findAll();
	    } else {
	        return sotGppRenameFieldsMappingRepository.searchBySotAndGppRename(sotRename, gppRename);
	    }
	}
	
	public SotGppRenameFieldsMappingDto getMappingById(int sotGppRid) {
	    SotGppRenameFieldsMapping entity = sotGppRenameFieldsMappingRepository.findById(sotGppRid)
	            .orElseThrow(() -> new IllegalArgumentException("SOT-GPP Mapping not found."));
	    return convertToDto(entity);
	}

	
	public SotGppRenameFieldsMappingDto convertToDto(SotGppRenameFieldsMapping entity) {
	    SotGppRenameFieldsMappingDto dto = new SotGppRenameFieldsMappingDto();
	    dto.setSotRid(entity.getSotFieldDetails().getSotRid());
	    dto.setGppRid(entity.getGppFieldDetails().getGppRid());
	    dto.setSotGppRemark(entity.getSotGppRemark());
	    
	    // Assuming gppSheet is a GppSheet entity and you want to set its ID in the DTO
	    if (entity.getGppSheet() != null) {
	        dto.setGppsheetRid(entity.getGppSheet().getGppSheetRid());
	    }
	    
	    dto.setCurrentUserId(entity.getCreatedBy()); // Assuming this represents current user ID

	    return dto;
	}
	
	 public boolean existsById(int sotGppRid) {
	        return sotGppRenameFieldsMappingRepository.existsById(sotGppRid);
	    }

	    public void deleteById(int sotGppRid) {
	        sotGppRenameFieldsMappingRepository.deleteById(sotGppRid);
	    }

}
