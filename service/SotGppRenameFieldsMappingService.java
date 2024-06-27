package com.optum.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optum.dao.GppFieldDetailsDao;
import com.optum.dao.SotFieldDetailsDao;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dto.SotGppRenameFieldsMappingDto;
import com.optum.entity.GppFieldDetails;
import com.optum.entity.SotFieldDetails;
import com.optum.entity.SotGppRenameFieldsMapping;

@Service
public class SotGppRenameFieldsMappingService {

    @Autowired
    private SotGppRenameFieldsMappingDao repository;

    @Autowired
    private SotFieldDetailsDao sotFieldDetailsRepository;

    @Autowired
    private GppFieldDetailsDao gppFieldDetailsRepository;

    @Autowired
    private AuditTrailService auditTrailService;

    public SotGppRenameFieldsMapping createMapping(SotGppRenameFieldsMappingDto dto) {
        SotGppRenameFieldsMapping mapping = new SotGppRenameFieldsMapping();
        SotFieldDetails sotFieldDetails = sotFieldDetailsRepository.findBySotFieldRename(dto.getSotFieldRename());
        GppFieldDetails gppFieldDetails = gppFieldDetailsRepository.findByGppFieldRename(dto.getGppFieldRename());

        mapping.setSotFieldDetails(sotFieldDetails);
        mapping.setGppFieldDetails(gppFieldDetails);
        mapping.setSotGppRemark(dto.getSotGppRemark());
        mapping.setCreatedBy(dto.getCurrentUserId());
        mapping.setModifiedBy(dto.getCurrentUserId());
        mapping.setCreatedDate(new Date());
        mapping.setModifiedDate(new Date());

        SotGppRenameFieldsMapping savedMapping = repository.save(mapping);

        // Log audit trail asynchronously
        CompletableFuture.runAsync(() -> {
            String details = String.format(
                "SotFieldRename: %s, GppFieldRename: %s, Remark: %s",
                savedMapping.getSotFieldDetails().getSotFieldRename(),
                savedMapping.getGppFieldDetails().getGppFieldRename(),
                savedMapping.getSotGppRemark()
            );
            auditTrailService.logAuditTrailWithUsername("Mapping Created", "SUCCESS", details, dto.getCurrentUserId());
        });

        return savedMapping;
    }

    public SotGppRenameFieldsMapping updateMapping(int id, SotGppRenameFieldsMappingDto dto) {
        SotGppRenameFieldsMapping existingMapping = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mapping not found"));

        SotFieldDetails sotFieldDetails = sotFieldDetailsRepository.findBySotFieldRename(dto.getSotFieldRename());
        GppFieldDetails gppFieldDetails = gppFieldDetailsRepository.findByGppFieldRename(dto.getGppFieldRename());

        existingMapping.setSotFieldDetails(sotFieldDetails);
        existingMapping.setGppFieldDetails(gppFieldDetails);
        existingMapping.setSotGppRemark(dto.getSotGppRemark());
        existingMapping.setModifiedBy(dto.getCurrentUserId());
        existingMapping.setModifiedDate(new Date());

        SotGppRenameFieldsMapping updatedMapping = repository.save(existingMapping);

        // Log audit trail asynchronously
        CompletableFuture.runAsync(() -> {
            String details = String.format(
                "Old SotFieldRename: %s, Old GppFieldRename: %s, Old Remark: %s; New SotFieldRename: %s, New GppFieldRename: %s, New Remark: %s",
                existingMapping.getSotFieldDetails().getSotFieldRename(),
                existingMapping.getGppFieldDetails().getGppFieldRename(),
                existingMapping.getSotGppRemark(),
                updatedMapping.getSotFieldDetails().getSotFieldRename(),
                updatedMapping.getGppFieldDetails().getGppFieldRename(),
                updatedMapping.getSotGppRemark()
            );
            auditTrailService.logAuditTrailWithUsername("Mapping Updated", "SUCCESS", details, dto.getCurrentUserId());
        });

        return updatedMapping;
    }

    public List<SotGppRenameFieldsMapping> getAllMappings() {
        return repository.findAll();
    }

    public SotGppRenameFieldsMapping getMappingById(int id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mapping not found"));
    }

    public List<SotGppRenameFieldsMapping> searchMappings(String keyword) {
        return repository.findBySotFieldDetails_SotFieldRenameContainingOrGppFieldDetails_GppFieldRenameContaining(keyword, keyword);
    }
}
