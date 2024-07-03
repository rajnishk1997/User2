package com.optum.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dao.SotJsonDao;
import com.optum.dao.GppJsonDao;
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

@Service
public class ComparisonService {

    @Autowired
    private SotGppRenameFieldsMappingDao mappingRepository;

    @Autowired
    private SotJsonDao sotJsonRepository;

    @Autowired
    private GppJsonDao gppJsonRepository;

    @Autowired
    private SOTNetworkMasterDao networkMappingRepository; // New DAO for network mapping

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<GppFieldValidationResponse> compareSotAndGppJson(int sotJsonId, int gppJsonId) {
        try {
            // Fetch SOT JSON data by sotJsonId
            String sotJson = sotJsonRepository.findById(sotJsonId)
                    .orElseThrow(() -> new RuntimeException("SOT JSON not found")).getJsonData();
            Map<String, Object> sotJsonMap = objectMapper.readValue(sotJson,
                    new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> dataRecords = (List<Map<String, Object>>) sotJsonMap.get("dataRecords");

            // Fetch GPP JSON data by gppJsonId
            String gppJson = gppJsonRepository.findById(gppJsonId)
                    .orElseThrow(() -> new RuntimeException("GPP JSON not found")).getJsonData();
            List<Map<String, Object>> gppJsonList = objectMapper.readValue(gppJson,
                    new TypeReference<List<Map<String, Object>>>() {});

            // Fetch mappings from database
            List<SotGppRenameFieldsMapping> mappings = mappingRepository.findAll();

            // Prepare response list
            List<GppFieldValidationResponse> responseList = new ArrayList<>();

            // Iterate over each dataRecord in SOT JSON
            for (Map<String, Object> sotRecord : dataRecords) {
                String listName = (String) sotRecord.get("LIST_NAME");
                String sotNetwork = (String) sotRecord.get("NETWORK");

                // Get corresponding GPP network name from SOTNetworkMaster
                SOTNetworkMaster networkMapping = networkMappingRepository.findBySotNetworkName(sotNetwork);
                String gppNetworkName = networkMapping.getsGppNetworkName();

                // Fetch GPP records based on LIST_NAME and GPP_network_name
                List<Map<String, Object>> filteredGppJsonList = filterGppJsonList(gppJsonList, listName, gppNetworkName);

                // Iterate over each filtered GPP JSON object
                for (Map<String, Object> gppObject : filteredGppJsonList) {
                    GppFieldValidationResponse response = new GppFieldValidationResponse();
                    Map<String, GppFieldValidationResponse.FieldValidation> gppFields = new HashMap<>();

                    // Iterate over each field in the GPP JSON object
                    for (Map.Entry<String, Object> gppEntry : gppObject.entrySet()) {
                        String gppFieldName = gppEntry.getKey();
                        Object gppFieldValue = gppEntry.getValue();

                        GppFieldValidationResponse.FieldValidation validation = new GppFieldValidationResponse.FieldValidation();
                        validation.setValue(gppFieldValue);
                        validation.setValidationStatus(null); // Default to null, will update if there's a mapping

                        // Check if there's a corresponding SOT field based on mappings
                        for (SotGppRenameFieldsMapping mapping : mappings) {
                            SotFieldDetails sotFieldDetails = mapping.getSotFieldDetails();
                            GppFieldDetails gppFieldDetails = mapping.getGppFieldDetails();

                            String sotFieldRename = sotFieldDetails.getSotFieldRename();
                            String gppFieldRename = gppFieldDetails.getGppFieldRename();

                            if (gppFieldName.equals(gppFieldRename)) {
                                Object sotValue = sotRecord.get(sotFieldRename);

                                if (sotValue != null && sotValue.equals(gppFieldValue)) {
                                    validation.setValidationStatus("valid");
                                } else {
                                    validation.setValidationStatus("invalid");
                                    validation.setExpectedValue(sotValue);
                                }
                                break;
                            }
                        }

                        gppFields.put(gppFieldName, validation);
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

    private List<Map<String, Object>> filterGppJsonList(List<Map<String, Object>> gppJsonList, String listName, String gppNetworkName) {
        List<Map<String, Object>> filteredList = new ArrayList<>();
        for (Map<String, Object> gppObject : gppJsonList) {
            String gppListName = gppObject.get("LIST_NAME") != null ? gppObject.get("LIST_NAME").toString() : "";
            String gppNetwork = gppObject.get("NETWORK") != null ? gppObject.get("NETWORK").toString() : "";

            if (listName.equals(gppListName) && gppNetworkName.equals(gppNetwork)) {
                filteredList.add(gppObject);
            }
        }
        return filteredList;
    }
}


