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
        // Check if mapping already exists
        SotFieldDetails sotFieldDetails = sotFieldDetailsRepository.findById(createDto.getSotRid())
                .orElseThrow(() -> new IllegalArgumentException("SOT Field not found."));
        GppFieldDetails gppFieldDetails = gppFieldDetailsRepository.findById(createDto.getGppRid())
                .orElseThrow(() -> new IllegalArgumentException("GPP Field not found."));

        // Check if a mapping with the same SOT or GPP field already exists
        SotGppRenameFieldsMapping existingSotMapping = sotGppRenameFieldsMappingRepository.findBySotFieldDetails(sotFieldDetails);
        SotGppRenameFieldsMapping existingGppMapping = sotGppRenameFieldsMappingRepository.findByGppFieldDetails(gppFieldDetails);

        if (existingSotMapping != null) {
            throw new IllegalArgumentException("Mapping with SOTRename is already done with GppRename = " +
                    existingSotMapping.getGppFieldDetails().getGppFieldRename());
        }
        if (existingGppMapping != null) {
            throw new IllegalArgumentException("Mapping with GppRename is already done with SOTRename = " +
                    existingGppMapping.getSotFieldDetails().getSotFieldRename());
        }
        SotGppRenameFieldsMapping newMapping = new SotGppRenameFieldsMapping();
        newMapping.setSotFieldDetails(sotFieldDetails);
        newMapping.setGppFieldDetails(gppFieldDetails);
        newMapping.setSotGppRemark(createDto.getSotGppRemark());
        newMapping.setGppSheet(createDto.getGppSheet());
        newMapping.setCreatedBy(createDto.getCurrentUserId());
        newMapping.setCreatedDate(new Date());
        newMapping.setModifiedDate(new Date());

        SotGppRenameFieldsMapping savedMapping = sotGppRenameFieldsMappingRepository.save(newMapping);

        logAuditTrail(savedMapping, null, null, null, createDto.getCurrentUserId(), true);
        ReqRes reqRes = new ReqRes(HttpStatus.CREATED.value(), "SUCCESS", "SOT-GPP Mapping created successfully");
        return new ResponseWrapper<>(savedMapping, reqRes);
    }

    @Transactional
    public ResponseWrapper<SotGppRenameFieldsMapping> updateSotGppMapping(int sotGppRid, SotGppRenameFieldsMappingDto updateDto) {
        SotGppRenameFieldsMapping existingMapping = sotGppRenameFieldsMappingRepository.findById(sotGppRid)
                .orElseThrow(() -> new IllegalArgumentException("SOT-GPP Mapping not found."));

        String oldSotFieldRename = existingMapping.getSotFieldDetails().getSotFieldRename();
        String oldGppFieldRename = existingMapping.getGppFieldDetails().getGppFieldRename();
        String oldSotGppRemark = existingMapping.getSotGppRemark();

        // Check if new sotRid and gppRid are already in use
        SotFieldDetails sotFieldDetails = sotFieldDetailsRepository.findById(updateDto.getSotRid())
                .orElseThrow(() -> new IllegalArgumentException("SOT Field not found."));
        GppFieldDetails gppFieldDetails = gppFieldDetailsRepository.findById(updateDto.getGppRid())
                .orElseThrow(() -> new IllegalArgumentException("GPP Field not found."));

        // Check if a mapping with the same SOT or GPP field already exists
        SotGppRenameFieldsMapping existingSotMapping = sotGppRenameFieldsMappingRepository.findBySotFieldDetails(sotFieldDetails);
        SotGppRenameFieldsMapping existingGppMapping = sotGppRenameFieldsMappingRepository.findByGppFieldDetails(gppFieldDetails);

        if (existingSotMapping != null && existingSotMapping.getSotGppRid() != sotGppRid) {
            throw new IllegalArgumentException("Mapping with SOTRename is already done with GppRename = " +
                    existingSotMapping.getGppFieldDetails().getGppFieldRename());
        }
        if (existingGppMapping != null && existingGppMapping.getSotGppRid() != sotGppRid) {
            throw new IllegalArgumentException("Mapping with GppRename is already done with SOTRename = " +
                    existingGppMapping.getSotFieldDetails().getSotFieldRename());
        }

        existingMapping.setSotFieldDetails(sotFieldDetails);
        existingMapping.setGppFieldDetails(gppFieldDetails);
        existingMapping.setSotGppRemark(updateDto.getSotGppRemark());
        existingMapping.setGppSheet(updateDto.getGppSheet());
        existingMapping.setModifiedBy(updateDto.getCurrentUserId());
        existingMapping.setModifiedDate(new Date());

        SotGppRenameFieldsMapping updatedMapping = sotGppRenameFieldsMappingRepository.save(existingMapping);

        logAuditTrail(updatedMapping, oldSotFieldRename, oldGppFieldRename, oldSotGppRemark, updateDto.getCurrentUserId(), false);

       ReqRes reqRes = new ReqRes(HttpStatus.OK.value(), "SUCCESS", "SOT-GPP Mapping updated successfully");
        return new ResponseWrapper<>(updatedMapping, reqRes);
    }

    private void logAuditTrail(SotGppRenameFieldsMapping mapping, String oldSotFieldRename, String oldGppFieldRename, String oldSotGppRemark, Integer currentUserRid, boolean isCreation) {
        CompletableFuture.runAsync(() -> {
            String action = isCreation ? "SOT-GPP Mapping Created" : "SOT-GPP Mapping Updated";
            String status = isCreation ? "CREATED" : "UPDATED";

            String details;
            if (isCreation) {
                details = String.format("SOT Field Rename: %s, GPP Field Rename: %s, SOT-GPP Remark: %s",
                        mapping.getSotFieldDetails().getSotFieldRename(),
                        mapping.getGppFieldDetails().getGppFieldRename(),
                        mapping.getSotGppRemark());
            } else {
                String oldDetails = String.format("Old SOT Field Rename: %s, Old GPP Field Rename: %s, Old SOT-GPP Remark: %s",
                        oldSotFieldRename, oldGppFieldRename, oldSotGppRemark);
                String newDetails = String.format("New SOT Field Rename: %s, New GPP Field Rename: %s, New SOT-GPP Remark: %s",
                        mapping.getSotFieldDetails().getSotFieldRename(),
                        mapping.getGppFieldDetails().getGppFieldRename(),
                        mapping.getSotGppRemark());
                details = oldDetails + "; " + newDetails;
            }

            auditTrailService.logAuditTrailWithUsername(action, status, details, currentUserRid);
        });
    }

    public List<SotGppRenameFieldsMapping> searchMappings(String sotRename, String gppRename) {
        if (sotRename == null && gppRename == null) {
            return sotGppRenameFieldsMappingRepository.findAll(); 
        } else {
            return sotGppRenameFieldsMappingRepository.searchBySotAndGppRename(sotRename, gppRename);
        }
    }

}
