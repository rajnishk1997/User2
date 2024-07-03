package com.optum.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.GppJsonDao;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dao.SotJsonDao;
import com.optum.dto.response.GppFieldValidationResponse;
import com.optum.entity.GppSheet;
import com.optum.entity.SotGppRenameFieldsMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComparisonService {

    @Autowired
    private SotGppRenameFieldsMappingDao mappingRepository;

    @Autowired
    private SotJsonDao sotJsonRepository;

    @Autowired
    private GppJsonDao gppJsonRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<GppFieldValidationResponse> compareJsonFromDb(int sotJsonId, int gppJsonId, GppSheet gppSheet) {
        try {
            // Retrieve JSON data from database
            String sotJson = sotJsonRepository.findById(sotJsonId).orElseThrow(() -> new RuntimeException("SOT JSON not found")).getJsonData();
            String gppJson = gppJsonRepository.findById(gppJsonId).orElseThrow(() -> new RuntimeException("GPP JSON not found")).getJsonData();

            List<Map<String, Object>> sotList = objectMapper.readValue(sotJson, List.class);
            List<Map<String, Object>> gppList = objectMapper.readValue(gppJson, List.class);
            List<SotGppRenameFieldsMapping> mappings = mappingRepository.findByGppSheet(gppSheet);

            List<GppFieldValidationResponse> responses = new ArrayList<>();

            for (Map<String, Object> sot : sotList) {
                for (Map<String, Object> gpp : gppList) {
                    responses.add(generateValidationResponse(sot, gpp, mappings));
                }
            }
            return responses;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private GppFieldValidationResponse generateValidationResponse(Map<String, Object> sot, Map<String, Object> gpp, List<SotGppRenameFieldsMapping> mappings) {
        GppFieldValidationResponse response = new GppFieldValidationResponse();
        Map<String, GppFieldValidationResponse.FieldValidation> gppFields = new HashMap<>();

        // Populate the GPP fields with their values and validation statuses
        for (Map.Entry<String, Object> entry : gpp.entrySet()) {
            String gppFieldName = entry.getKey();
            Object gppValue = entry.getValue();

            GppFieldValidationResponse.FieldValidation validation = new GppFieldValidationResponse.FieldValidation();
            validation.setValue(gppValue);

            // Check if this field has a mapping
            SotGppRenameFieldsMapping mapping = mappings.stream()
                .filter(m -> m.getGppFieldDetails().getGppFieldName().equals(gppFieldName))
                .findFirst()
                .orElse(null);

            if (mapping != null) {
                String sotFieldName = mapping.getSotFieldDetails().getSotFieldName();
                Object sotValue = sot.get(sotFieldName);
                if (sotValue != null && sotValue.equals(gppValue)) {
                    validation.setValidationStatus("valid");
                } else {
                    validation.setValidationStatus("invalid");
                    validation.setExpectedValue(sotValue);
                }
            }

            gppFields.put(gppFieldName, validation);
        }

        response.setGppFields(gppFields);
        return response;
    }
}

