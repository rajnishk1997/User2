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
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class SOTNewtorkMasterService {
	
	@Autowired
	private SOTNetworkMasterDao sotNetworkMasterRepository;

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
                platform.setsPlatformName(name.trim());
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
            sotNetworkMaster.setsPlatform(sPlatform);
            sotNetworkMaster.setsCreatedBy(sCreatedBy);
            sotNetworkMaster.setsCreatedDatetime(sCreatedDatetime);
            return sotNetworkMasterRepository.save(sotNetworkMaster);
        } catch (Exception e) {
            // Log and handle the exception
            throw new RuntimeException("Failed to save network information.", e);
        }
    }
		
}

