package com.optum.configuration;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.optum.dao.SPlatformDao;
import com.optum.entity.SPlatform;

@Configuration
@PropertySource("classpath:platformnames.properties")
public class PlatformConfig {

    private final SPlatformDao platformRepository;

    @Value("${platformnames}")
    private String platformNames;

    public PlatformConfig(SPlatformDao platformRepository) {
        this.platformRepository = platformRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        List<String> platformNamesList = Arrays.asList(platformNames.split(","));
        for (String platformName : platformNamesList) {
            if (!isPlatformExists(platformName.trim())) {
                SPlatform platform = new SPlatform();
                platform.setPlatformName(platformName.trim());
                platform.setsCreatedDatetime(new Date());
                platformRepository.save(platform);
            }
        }
    }

    private boolean isPlatformExists(String platformName) {
    	  // Query the repository for a platform with the given name
        SPlatform platform = platformRepository.findBySPlatformName(platformName);
        
        // Return true if a platform with the given name exists, false otherwise
        return platform != null;
    }
}
