package com.optum.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optum.dao.GppFieldDetailsDao;
import com.optum.dao.ReqRes;
import com.optum.dao.SotFieldDetailsDao;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dto.SotGppRenameFieldsMappingDto;
import com.optum.entity.GppFieldDetails;
import com.optum.entity.ResponseWrapper;
import com.optum.entity.SotFieldDetails;
import com.optum.entity.SotGppRenameFieldsMapping;

@Service
public class SotGppRenameFieldsMappingService {

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
		// Check if SOT Field exists
		SotFieldDetails sotFieldDetails = sotFieldDetailsRepository.findById(createDto.getSotRid())
				.orElseThrow(() -> new IllegalArgumentException("SOT Field not found."));

		// Check if GPP Field exists
		GppFieldDetails gppFieldDetails = gppFieldDetailsRepository.findById(createDto.getGppRid())
				.orElseThrow(() -> new IllegalArgumentException("GPP Field not found."));

		// Check if SOT Field is already mapped to another GPP Field
		SotGppRenameFieldsMapping existingSotMapping = sotGppRenameFieldsMappingRepository
				.findBySotFieldDetails(sotFieldDetails);
		if (existingSotMapping != null) {
			throw new IllegalArgumentException("SOT Field is already mapped to another GPP Field.");
		}

		// Check if GPP Field is already mapped to another SOT Field
		SotGppRenameFieldsMapping existingGppMapping = sotGppRenameFieldsMappingRepository
				.findByGppFieldDetails(gppFieldDetails);
		if (existingGppMapping != null) {
			throw new IllegalArgumentException("GPP Field is already mapped to another SOT Field.");
		}

		// Create new mapping
		SotGppRenameFieldsMapping newMapping = new SotGppRenameFieldsMapping();
		newMapping.setSotFieldDetails(sotFieldDetails);
		newMapping.setGppFieldDetails(gppFieldDetails);
		newMapping.setSotGppRemark(createDto.getSotGppRemark());
		newMapping.setGppSheet(createDto.getGppSheet());
		newMapping.setCreatedBy(createDto.getCurrentUserId());
		newMapping.setCreatedDate(new Date());
		newMapping.setModifiedDate(new Date());

		SotGppRenameFieldsMapping savedMapping = sotGppRenameFieldsMappingRepository.save(newMapping);
		 logAuditTrail(savedMapping, null, null, null, null, createDto.getCurrentUserId(), true);

		// Return response wrapper
		ReqRes reqRes = new ReqRes(HttpStatus.CREATED.value(), "SUCCESS", "SOT-GPP Mapping created successfully");
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
		String oldSheet = existingMapping.getGppSheet();

		// Check if new sotRid and gppRid are already in use
		SotFieldDetails sotFieldDetails = sotFieldDetailsRepository.findById(updateDto.getSotRid())
				.orElseThrow(() -> new IllegalArgumentException("SOT Field not found."));
		GppFieldDetails gppFieldDetails = gppFieldDetailsRepository.findById(updateDto.getGppRid())
				.orElseThrow(() -> new IllegalArgumentException("GPP Field not found."));

		SotGppRenameFieldsMapping existingSotMapping = sotGppRenameFieldsMappingRepository
				.findBySotFieldDetails(sotFieldDetails);
		if (existingSotMapping != null && existingSotMapping.getSotGppRid() != sotGppRid) {
			throw new IllegalArgumentException("SOT Field is already mapped to another GPP Field.");
		}

		SotGppRenameFieldsMapping existingGppMapping = sotGppRenameFieldsMappingRepository
				.findByGppFieldDetails(gppFieldDetails);
		if (existingGppMapping != null && existingGppMapping.getSotGppRid() != sotGppRid) {
			throw new IllegalArgumentException("GPP Field is already mapped to another SOT Field.");
		}

		// Update existing mapping
		existingMapping.setSotFieldDetails(sotFieldDetails);
		existingMapping.setGppFieldDetails(gppFieldDetails);
		existingMapping.setSotGppRemark(updateDto.getSotGppRemark());
		existingMapping.setGppSheet(updateDto.getGppSheet());
		existingMapping.setModifiedBy(updateDto.getCurrentUserId());
		existingMapping.setModifiedDate(new Date());

		SotGppRenameFieldsMapping updatedMapping = sotGppRenameFieldsMappingRepository.save(existingMapping);

		// Log audit trail with old and new values
		logAuditTrail(updatedMapping, oldSotFieldDetails, oldGppFieldDetails, oldRemark, oldSheet,
				updateDto.getCurrentUserId(), false);

		// Return response wrapper
		ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "SOT-GPP Mapping updated successfully");
		return new ResponseWrapper<>(updatedMapping, reqRes);
	}

	private void logAuditTrail(SotGppRenameFieldsMapping newMapping, SotFieldDetails oldSotFieldDetails,
			GppFieldDetails oldGppFieldDetails, String oldRemark, String oldSheet, Integer currentUserRid,
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
						oldSheet);
				String newDetails = String.format(
						"New SOT Field Rename: %s, New GPP Field Rename: %s, New SOT-GPP Remark: %s, New GPP Sheet: %s",
						newMapping.getSotFieldDetails().getSotFieldRename(),
						newMapping.getGppFieldDetails().getGppFieldRename(), newMapping.getSotGppRemark(),
						newMapping.getGppSheet());
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


}
