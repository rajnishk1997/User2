package com.optum.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import com.optum.dao.GppSheetDao;
import com.optum.entity.GppSheet;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Configuration
@PropertySource({"classpath:application.properties", "classpath:sheet.properties"}) 
public class GppSheetConfig {

    private final GppSheetDao gppSheetRepository;

    @Value("${sheets}")
    private String sheets;

    @Autowired
    public GppSheetConfig(GppSheetDao gppSheetRepository) {
        this.gppSheetRepository = gppSheetRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        List<String> sheetNames = Arrays.asList(sheets.split(", "));
        for (String sheetName : sheetNames) {
            GppSheet gppSheet = gppSheetRepository.findByGppSheetName(sheetName);
            if (gppSheet == null) {
                gppSheet = new GppSheet(sheetName, true);
                gppSheet.setCreatedDate(new Date());
                gppSheetRepository.save(gppSheet);
            }
        }
    }
}

