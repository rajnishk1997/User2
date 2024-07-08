package com.optum.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optum.dao.GppFieldDetailsDao;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dto.GppFieldDetailsDto;
import com.optum.dto.GppRenameDto;
import com.optum.entity.GppFieldDetails;
import com.optum.entity.SotFieldDetails;
import com.optum.entity.SotGppRenameFieldsMapping;

@Service
public class GppFieldDetailsService {

    @Autowired
    private GppFieldDetailsDao gppFieldDetailsRepository;

    @Autowired
    private AuditTrailService auditTrailService;
    @Autowired
   	private SotGppRenameFieldsMappingDao sotGppRenameFieldsMappingRepository;

    public GppFieldDetails createGppFieldDetails(GppFieldDetailsDto gppFieldDetailsDto, Integer currentUserRid) {
        GppFieldDetails gppFieldDetails = new GppFieldDetails();
        gppFieldDetails.setGppFieldName(gppFieldDetailsDto.getGppFieldName());
        gppFieldDetails.setGppFieldRename(gppFieldDetailsDto.getGppFieldRename());
        gppFieldDetails.setGPPValidationRequired(gppFieldDetailsDto.isGPPValidationRequired());
        gppFieldDetails.setCreatedBy(currentUserRid);
        gppFieldDetails.setCreatedDate(new Date());

        GppFieldDetails savedGppFieldDetails = gppFieldDetailsRepository.save(gppFieldDetails);

        // Log audit trail asynchronously
        CompletableFuture.runAsync(() -> {
            String details = String.format(
                "GppFieldName: %s, GppFieldRename: %s, GPPValidationRequired: %s",
                savedGppFieldDetails.getGppFieldName(),
                savedGppFieldDetails.getGppFieldRename(),
                savedGppFieldDetails.isGPPValidationRequired()
            );
            auditTrailService.logAuditTrailWithUsername("GPP Field Created", "SUCCESS", details, currentUserRid);
        });

        return savedGppFieldDetails;
    }

    public GppFieldDetails updateGppFieldDetails(int id, GppFieldDetailsDto gppFieldDetailsDto, Integer currentUserRid) {
        GppFieldDetails existingGppFieldDetails = getGppFieldDetailsById(id);

        // Capture old values before updating
        String oldGppFieldName = existingGppFieldDetails.getGppFieldName();
        String oldGppFieldRename = existingGppFieldDetails.getGppFieldRename();
        boolean oldGPPValidationRequired = existingGppFieldDetails.isGPPValidationRequired();

        // Update the entity
        existingGppFieldDetails.setGppFieldName(gppFieldDetailsDto.getGppFieldName());
        existingGppFieldDetails.setGppFieldRename(gppFieldDetailsDto.getGppFieldRename());
        existingGppFieldDetails.setGPPValidationRequired(gppFieldDetailsDto.isGPPValidationRequired());
        existingGppFieldDetails.setModifiedBy(currentUserRid);
        existingGppFieldDetails.setModifiedDate(new Date());

        GppFieldDetails updatedGppFieldDetails = gppFieldDetailsRepository.save(existingGppFieldDetails);

        // Log audit trail asynchronously
        CompletableFuture.runAsync(() -> {
            String oldDetails = String.format(
                "Old GppFieldName: %s, Old GppFieldRename: %s, Old GPPValidationRequired: %s",
                oldGppFieldName,
                oldGppFieldRename,
                oldGPPValidationRequired
            );
            String newDetails = String.format(
                "New GppFieldName: %s, New GppFieldRename: %s, New GPPValidationRequired: %s",
                updatedGppFieldDetails.getGppFieldName(),
                updatedGppFieldDetails.getGppFieldRename(),
                updatedGppFieldDetails.isGPPValidationRequired()
            );
            String details = oldDetails + "; " + newDetails;
            auditTrailService.logAuditTrailWithUsername("GPP Field Updated", "SUCCESS", details, currentUserRid);
        });

        return updatedGppFieldDetails;
    }

    public List<GppFieldDetails> getAllGppFieldDetails() {
        return gppFieldDetailsRepository.findAll();
    }

    public GppFieldDetails getGppFieldDetailsById(int id) {
        return gppFieldDetailsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("GPP Field not found"));
    }

    public List<GppFieldDetails> searchGppFieldDetails(String keyword, Boolean validation) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllGppFieldDetails();
        }
        return gppFieldDetailsRepository.searchByKeywordAndValidation(keyword, validation);
    }

	 public List<GppRenameDto> getAllGppRenames() {
        return gppFieldDetailsRepository.findAllGppRenames();
    }
	 
	  public List<SotFieldDetails> getMappedSotFieldsByGppRid(int gppRid) {
	        List<SotGppRenameFieldsMapping> mappings = sotGppRenameFieldsMappingRepository.findByGppFieldDetails_GppRid(gppRid);
	        return mappings.stream()
	                       .map(SotGppRenameFieldsMapping::getSotFieldDetails)
	                       .collect(Collectors.toList());
	    }
	    
	    public boolean deleteGppFieldById(int gppRid) {
	        // First check for mappings
	        List<SotFieldDetails> sotFieldDetailsList = getMappedSotFieldsByGppRid(gppRid);
	        if (!sotFieldDetailsList.isEmpty()) {
	            return false;
	        }

	        // If no mappings, proceed with delete
	        gppFieldDetailsRepository.deleteById(gppRid);
	        return true;
	    }
}

