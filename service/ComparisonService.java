package com.optum.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dao.SotJsonDao;
import com.optum.dao.GppJsonDao;
import com.optum.dao.RxSotGppDataDao;
import com.optum.dao.SOTNetworkMasterDao;
import com.optum.dto.response.GppFieldValidationResponse;
import com.optum.entity.SotGppRenameFieldsMapping;
import com.optum.entity.SotFieldDetails;
import com.optum.entity.SotGppNetworkFieldMapping;
import com.optum.entity.GppFieldDetails;
import com.optum.entity.SOTNetworkMaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComparisonService {

    @Autowired
    private SotGppRenameFieldsMappingDao mappingRepository;

    @Autowired
    private SotJsonDao sotJsonRepository;

    @Autowired
    private GppJsonDao gppJsonRepository;

    @Autowired
    private SOTNetworkMasterDao networkMappingRepository; 
    
    @Autowired
    private RxSotGppDataDao rxSotGppDataRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public List<GppFieldValidationResponse> compareSotAndGppJson(int uid) {
        try {
            // Fetch SOT and GPP JSON data by uid
            Object[] jsonData = rxSotGppDataRepository.findJsonDataByUid(uid);
            if (jsonData == null || jsonData.length < 2) {
                throw new RuntimeException("Data not found for UID: " + uid);
            }

            String sotJson = (String) jsonData[1];
            String gppJson = (String) jsonData[0];

            List<Map<String, Object>> sotJsonList = objectMapper.readValue(sotJson,
                    new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> gppJsonList = objectMapper.readValue(gppJson,
                    new TypeReference<List<Map<String, Object>>>() {});

            // Fetch mappings from database based on sotRid and gppRid
            List<SotGppRenameFieldsMapping> mappings = mappingRepository.findAll();

            // Prepare response list
            List<GppFieldValidationResponse> responseList = new ArrayList<>();

            // Iterate over each SOT JSON dataRecord
            for (Map<String, Object> sotObject : sotJsonList) {
                String listName = (String) sotObject.get("LIST_NAME");
                String sotNetwork = (String) sotObject.get("NETWORK");

                // Get corresponding GPP network name from SOTNetworkMaster
                SOTNetworkMaster networkMapping = networkMappingRepository.findBySotNetworkName(sotNetwork);
                String gppNetworkName = networkMapping.getsGppNetworkName();

                // Filter GPP JSON objects based on LIST_NAME and NETWORK
                List<Map<String, Object>> filteredGppJsonList = gppJsonList.stream()
                        .filter(gppObject -> listName.equals(gppObject.get("LIST_NAME")) &&
                                gppNetworkName.equals(gppObject.get("NETWORK")))
                        .collect(Collectors.toList());

                // Compare and validate fields for each filtered GPP JSON object
                for (Map<String, Object> gppObject : filteredGppJsonList) {
                    GppFieldValidationResponse response = new GppFieldValidationResponse();
                    Map<String, GppFieldValidationResponse.FieldValidation> gppFields = new HashMap<>();

                    // Iterate over each mapping and compare fields in SOT and GPP JSON
                    for (SotGppRenameFieldsMapping mapping : mappings) {
                        SotFieldDetails sotFieldDetails = mapping.getSotFieldDetails();
                        GppFieldDetails gppFieldDetails = mapping.getGppFieldDetails();

                        String sotFieldRename = sotFieldDetails.getSotFieldRename();
                        String gppFieldRename = gppFieldDetails.getGppFieldRename();

                        // Add all GPP fields to the response with validation status
                        for (String key : gppObject.keySet()) {
                            GppFieldValidationResponse.FieldValidation validation = new GppFieldValidationResponse.FieldValidation();
                            Object gppValue = gppObject.get(key);
                            validation.setValue(gppValue);

                            if (sotFieldRename.equals(key)) {
                                Object sotValue = sotObject.get(sotFieldRename);
                                if (sotValue != null && sotValue.equals(gppValue)) {
                                    validation.setValidationStatus("valid");
                                } else {
                                    validation.setValidationStatus("invalid");
                                    validation.setExpectedValue(sotValue);
                                }
                            } else {
                                validation.setValidationStatus("valid");
                            }
                            gppFields.put(key, validation);
                        }
                    }

                    response.setGppFields(gppFields);
                    responseList.add(response);
                }
            }

            return responseList;
        } catch (IOException e) {
            throw new RuntimeException("Error processing JSON data", e);
        }
    }
}


