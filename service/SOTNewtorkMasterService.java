package com.optum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.optum.dao.SOTNetworkMasterDao;
import com.optum.dto.request.SOTNetworkMasterRequestDTO;
import com.optum.entity.SOTNetworkMaster;
import com.optum.entity.SPlatform;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class SOTNewtorkMasterService {
	
	@Autowired
	private SOTNetworkMasterDao sotNetworkMasterRepository;
	
	@Autowired
    private AuditTrailService auditTrailService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${platform.names}")
    private String platformNames;

    @Transactional
    public void addPlatformNames() {
        List<String> names = Arrays.asList(platformNames.split(","));
        for (String name : names) {
            if (!isPlatformExists(name.trim())) {
                SPlatform platform = new SPlatform();
                platform.setPlatformName(name.trim());
                platform.setsCreatedDatetime(new Date());
                entityManager.persist(platform);
            }
        }
    }

    public boolean isPlatformExists(String platformName) {
        String query = "SELECT COUNT(p) FROM SPlatform p WHERE p.sPlatformName = :name";
        Long count = (Long) entityManager.createQuery(query)
                .setParameter("name", platformName)
                .getSingleResult();
        return count > 0;
    }

	public SOTNetworkMaster saveNetworkInfo(SOTNetworkMasterRequestDTO sotNetworkMasterRequestDTO) {
		   // Extracting network information from the DTO
        String sSotNetworkName = sotNetworkMasterRequestDTO.getSSotNetworkName();
        String sGppNetworkName = sotNetworkMasterRequestDTO.getSGppNetworkName();
        SPlatform sPlatform = sotNetworkMasterRequestDTO.getSPlatform();
        Integer sCreatedBy = sotNetworkMasterRequestDTO.getCurrentUserId();
        Date sCreatedDatetime = new Date(); // Assuming current time as creation datetime

        try {
            SOTNetworkMaster sotNetworkMaster = new SOTNetworkMaster();
            sotNetworkMaster.setsSotNetworkName(sSotNetworkName);
            sotNetworkMaster.setsGppNetworkName(sGppNetworkName);
            sotNetworkMaster.setPlatform(sPlatform);
            sotNetworkMaster.setsCreatedBy(sCreatedBy);
            sotNetworkMaster.setsCreatedDatetime(sCreatedDatetime);
            return sotNetworkMasterRepository.save(sotNetworkMaster);
        } catch (Exception e) {
            // Log and handle the exception
            throw new RuntimeException("Failed to save network information.", e);
        }
    }

	public SOTNetworkMaster updateNetworkInfo(SOTNetworkMasterRequestDTO sotNetworkMasterRequestDTO) {
	    // Assuming the DTO contains an ID for the record to update
	    int sRid = sotNetworkMasterRequestDTO.getsRid();
	    Optional<SOTNetworkMaster> existingNetworkMasterOpt = sotNetworkMasterRepository.findById(sRid);

	    if (existingNetworkMasterOpt.isPresent()) {
	        SOTNetworkMaster existingNetworkMaster = existingNetworkMasterOpt.get();
	        
	        // Capture old values for logging
	        String oldValues = String.format(
	            "Old SOT Network Info: SOT Network Name: %s, GPP Network Name: %s, Platform: %s",
	            existingNetworkMaster.getsSotNetworkName(),
	            existingNetworkMaster.getsGppNetworkName(),
	            existingNetworkMaster.getPlatform().getPlatformName()
	        );

	        // Update the existing entity with new values
	        existingNetworkMaster.setsSotNetworkName(sotNetworkMasterRequestDTO.getsSotNetworkName());
	        existingNetworkMaster.setsGppNetworkName(sotNetworkMasterRequestDTO.getsGppNetworkName());
	        
	        // Fetch the SPlatform entity using the provided platformRid
	        int platformRid = sotNetworkMasterRequestDTO.getPlatformRid();
	        Optional<SPlatform> platformOpt = sPlatformRepository.findById(platformRid);
	        if (platformOpt.isPresent()) {
	            existingNetworkMaster.setPlatform(platformOpt.get());
	        } else {
	            throw new EntityNotFoundException("Platform with ID " + platformRid + " not found.");
	        }

	        existingNetworkMaster.setsModifiedBy(sotNetworkMasterRequestDTO.getCurrentUserId());
	        existingNetworkMaster.setsModifyDatetime(new Date());

	        SOTNetworkMaster updatedNetworkMaster = sotNetworkMasterRepository.save(existingNetworkMaster);

	        // Capture new values for logging
	        String newValues = String.format(
	            "New SOT Network Info: SOT Network Name: %s, GPP Network Name: %s, Platform: %s",
	            updatedNetworkMaster.getsSotNetworkName(),
	            updatedNetworkMaster.getsGppNetworkName(),
	            updatedNetworkMaster.getPlatform().getPlatformName()
	        );

	        // Log the audit trail with both old and new values
	        CompletableFuture.runAsync(() -> {
	            String details = String.format(
	                "Updated SOT Network Info. %s => %s",
	                oldValues, newValues
	            );
	            auditTrailService.logAuditTrailWithUsername("Network Info Updated", "SUCCESS", details, sotNetworkMasterRequestDTO.getCurrentUserId());
	        });

	        return updatedNetworkMaster;
	    } else {
	        throw new EntityNotFoundException("Network Master with ID " + sRid + " not found.");
	    }
	}

	
	  public List<SOTNetworkMasterRequestDTO> searchNetworkInfo(String keyword) {
	        try {
	            List<SOTNetworkMaster> entities = sotNetworkMasterRepository.searchByKeyword(keyword);
	            return entities.stream()
	                .map(this::convertToDTO)
	                .collect(Collectors.toList());
	        } catch (Exception e) {
	            // Log the exception
	            e.printStackTrace();
	            // Handle or rethrow as needed
	            throw new RuntimeException("Failed to search network information with keyword: " + keyword, e);
	        }
	    }
	  
    public SOTNetworkMaster getNetworkInfoBySRid(int sRid) {
        try {
            return sotNetworkMasterRepository.findById(sRid)
                    .orElseThrow(() -> new EntityNotFoundException("Network Master with ID " + sRid + " not found."));
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Handle or rethrow as needed
            throw new RuntimeException("Failed to get network information by sRid", e);
        }
    }

    public List<SOTNetworkMaster> getAllNetworkInfo() {
        try {
            return sotNetworkMasterRepository.findAll();
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Handle or rethrow as needed
            throw new RuntimeException("Failed to get all network information", e);
        }
    }


}

