package com.optum.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dao.SotJsonDao;
import com.optum.dao.GppJsonDao;
import com.optum.dto.response.GppFieldValidationResponse;
import com.optum.entity.SotGppRenameFieldsMapping;
import com.optum.entity.SotFieldDetails;
import com.optum.entity.GppFieldDetails;

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

    public List<GppFieldValidationResponse> compareSotAndGppJson(int sotJsonId, int gppJsonId) {
        try {
            // Fetch SOT JSON data by sotJsonId
            String sotJson = sotJsonRepository.findById(sotJsonId)
                    .orElseThrow(() -> new RuntimeException("SOT JSON not found")).getJsonData();
            List<Map<String, Object>> sotJsonList = objectMapper.readValue(sotJson,
                    new TypeReference<List<Map<String, Object>>>() {});

            // Fetch GPP JSON data by gppJsonId
            String gppJson = gppJsonRepository.findById(gppJsonId)
                    .orElseThrow(() -> new RuntimeException("GPP JSON not found")).getJsonData();
            List<Map<String, Object>> gppJsonList = objectMapper.readValue(gppJson,
                    new TypeReference<List<Map<String, Object>>>() {});

            // Fetch mappings from database based on sotRid and gppRid
            List<SotGppRenameFieldsMapping> mappings = mappingRepository.findAll();

            // Prepare response list
            List<GppFieldValidationResponse> responseList = new ArrayList<>();

            // Iterate over each GPP JSON object
            for (Map<String, Object> gppObject : gppJsonList) {
                GppFieldValidationResponse response = new GppFieldValidationResponse();
                Map<String, GppFieldValidationResponse.FieldValidation> gppFields = new HashMap<>();

                // Iterate over each mapping and compare fields in SOT and GPP JSON
                for (SotGppRenameFieldsMapping mapping : mappings) {
                    SotFieldDetails sotFieldDetails = mapping.getSotFieldDetails();
                    GppFieldDetails gppFieldDetails = mapping.getGppFieldDetails();

                    String sotFieldRename = sotFieldDetails.getSotFieldRename();
                    String gppFieldRename = gppFieldDetails.getGppFieldRename();

                    // Check if the SOT field rename exists in SOT JSON
                    if (containsField(sotJsonList, sotFieldRename)) {
                        Object sotValue = getSotValue(sotJsonList, sotFieldRename);
                        Object gppValue = gppObject.get(gppFieldRename);

                        // Create field validation object
                        GppFieldValidationResponse.FieldValidation validation = new GppFieldValidationResponse.FieldValidation();
                        validation.setValue(gppValue);

                        if (sotValue != null && sotValue.equals(gppValue)) {
                            validation.setValidationStatus("valid");
                        } else {
                            validation.setValidationStatus("invalid");
                            validation.setExpectedValue(sotValue);
                        }

                        gppFields.put(gppFieldRename, validation);
                    }
                }

                response.setGppFields(gppFields);
                responseList.add(response);
            }

            return responseList;
        } catch (IOException e) {
            throw new RuntimeException("Error processing JSON data", e);
        }
    }

    private boolean containsField(List<Map<String, Object>> jsonList, String fieldName) {
        for (Map<String, Object> jsonObject : jsonList) {
            if (jsonObject.containsKey(fieldName)) {
                return true;
            }
        }
        return false;
    }

    private Object getSotValue(List<Map<String, Object>> sotJsonList, String sotFieldRename) {
        for (Map<String, Object> sotObject : sotJsonList) {
            if (sotObject.containsKey(sotFieldRename)) {
                return sotObject.get(sotFieldRename);
            }
        }
        return null;
    }
}

