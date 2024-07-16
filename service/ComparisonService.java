package com.optum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.SotGppRenameFieldsMappingDao;
import com.optum.dao.SotJsonDao;
import com.optum.dao.GppJsonDao;
import com.optum.dao.ProjectSOTDao;
import com.optum.dao.SOTNetworkMasterDao;
import com.optum.dto.response.GppFieldValidationResponse;
import com.optum.dto.response.GppJson28FieldValidationResponse;
import com.optum.entity.SotGppRenameFieldsMapping;
import com.optum.entity.SotFieldDetails;
import com.optum.entity.SotGppNetworkFieldMapping;
import com.optum.entity.GppFieldDetails;
import com.optum.entity.SOTNetworkMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ComparisonService {

    @Autowired
    private SotGppRenameFieldsMappingDao mappingRepository;

    @Autowired
    private ProjectSOTDao projectSOTDao;
    
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private GppJson28Service gppJson28Service;

    @Autowired
    private SOTNetworkMasterDao networkMappingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Properties replacementProperties;
    private Map<String, String> networkMappings;

    public ComparisonService() throws IOException {
        Resource resource = new ClassPathResource("sot-replacements.properties");
        replacementProperties = PropertiesLoaderUtils.loadProperties(resource);
        loadNetworkMappings();
    }
    
    private void loadNetworkMappings() {
        List<SOTNetworkMaster> networkMasters = networkMappingRepository.findAll();
        networkMappings = new HashMap<>();
        for (SOTNetworkMaster networkMaster : networkMasters) {
            networkMappings.put(networkMaster.getsSotNetworkName(), networkMaster.getsGppNetworkName());
        }
    }

    public List<GppFieldValidationResponse> compareSotAndGppJson(int uid) {
        try {
        	// Call ServiceClass2 asynchronously
            CompletableFuture<List<GppJson28FieldValidationResponse>> gppJson28ResponseFuture = gppJson28Service.getGppJson28ByUidAsync(uid);
            // Fetch project details from rx_sot_project_detail table
            SOTProjectDetail sotProjectDetail = sotProjectDetailDao.findBySotId(sotId);
            if (sotProjectDetail == null) {
                throw new RuntimeException("Project details not found for SOT ID: " + sotId);
            }

            // Populate validateDTO with project details
            validateDTO.setClientName(sotProjectDetail.getClientName());
            validateDTO.setCagDetail(sotProjectDetail.getCagDetail());
            validateDTO.setProjectId(sotProjectDetail.getProjectId());
            validateDTO.setAuditDate(sotProjectDetail.getAuditDate());
            validateDTO.setSotId(sotId);
            // Fetch JSON data from the database
            Query query = entityManager.createNativeQuery("SELECT gpp_json4, sot_json FROM rxcl_audit.rx_sot_gpp_data WHERE uid = :uid");
            query.setParameter("uid", uid);
            List<Object[]> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                throw new RuntimeException("Data not found for UID: " + uid);
            }

            Object[] jsonData = resultList.get(0);
            String sotJson = (String) jsonData[1];
            String gppJson = (String) jsonData[0];

            List<Map<String, Object>> sotJsonList = objectMapper.readValue(sotJson, new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> gppJsonList = objectMapper.readValue(gppJson, new TypeReference<List<Map<String, Object>>>() {});
            
            Query query1 = entityManager.createNativeQuery("SELECT gpp_json28 FROM rxcl_audit.rx_sot_gpp_data WHERE uid = :uid");
            query.setParameter("uid", uid);
            String gppJson28String = (String) query1.getSingleResult();

            GppFieldValidationResponse response = new GppFieldValidationResponse();

            if (gppJson28String != null && !gppJson28String.isEmpty()) {
                List<Map<String, Object>> gppJson28List = parseJson(gppJson28String);
                response.setGppJson28Fields(processGppJson28(gppJson28List));
            }

            
            // Process SOT JSON data
            List<Map<String, Object>> processedSotJsonList = processSotJson(sotJsonList);

            // Fetch mappings from the database
            List<SotGppRenameFieldsMapping> mappings = mappingRepository.findAll();

         // Create a map to store SOT to GPP field mappings
            Map<String, List<String>> sotToGppFieldMap = new HashMap<>();
            for (SotGppRenameFieldsMapping mapping : mappings) {
                SotFieldDetails sotFieldDetails = mapping.getSotFieldDetails();
                GppFieldDetails gppFieldDetails = mapping.getGppFieldDetails();

                String sotFieldRename = sotFieldDetails.getSotFieldRename();
                String gppFieldRename = gppFieldDetails.getGppFieldRename();

                sotToGppFieldMap.computeIfAbsent(sotFieldRename, k -> new ArrayList<>()).add(gppFieldRename);
            }

            // Prepare the response list
            List<GppFieldValidationResponse> responseList = new ArrayList<>();
           
            // Iterate over each processed SOT JSON data record
            for (Map<String, Object> sotRecord : processedSotJsonList) {
                String listName = (String) sotRecord.get("LIST_NAME");
                String sotNetwork = (String) sotRecord.get("NETWORK");

                // Get corresponding GPP network name from the pre-loaded network mappings
                String gppNetworkName = networkMappings.get(sotNetwork);

                // Replace NETWORK field in processedSotJsonList with gppNetworkName
                sotRecord.put("NETWORK", gppNetworkName);

                // Filter GPP JSON objects based on LIST_NAME and NETWORK
                List<Map<String, Object>> filteredGppJsonList = gppJsonList.stream()
                        .filter(gppObject -> listName.equals(gppObject.get("LIST_NAME")) &&
                                gppNetworkName.equals(gppObject.get("NETWORK")))
                        .collect(Collectors.toList());

                // Compare and validate fields for each filtered GPP JSON object
                for (Map<String, Object> gppObject : filteredGppJsonList) {
                	 int trueCount = 0, falseCount = 0, nullCount = 0;
                    GppFieldValidationResponse response = new GppFieldValidationResponse();
                    Map<String, GppFieldValidationResponse.FieldValidation> gppFields = new HashMap<>();

                    // Iterate over each SOT field and its mapped GPP fields
                    for (Map.Entry<String, List<String>> entry : sotToGppFieldMap.entrySet()) {
                        String sotFieldRename = entry.getKey();
                        List<String> gppFieldRenames = entry.getValue();

                        // Add all GPP fields to the response with validation status
                        for (String key : gppObject.keySet()) {
                            GppFieldValidationResponse.FieldValidation validation = new GppFieldValidationResponse.FieldValidation();
                            Object gppValue = gppObject.get(key);
                            validation.setValue(gppValue);

                            if (gppFieldRenames.contains(key)) {
                                Object sotValue = sotRecord.get(sotFieldRename);
                                if (sotValue != null && sotValue.equals(gppValue)) {
                                    validation.setValidationStatus("true");
                                    trueCount++;
                                    break;
                                } else {
                                    validation.setValidationStatus("false");
                                    falseCount++;
                                    validation.setExpectedValue(sotValue);
                                }
                            } else {
                                validation.setValidationStatus("null");
                                nullCount++;
                            }
                            gppFields.put(key, validation);
                        }
                    }

                    response.setGppFields(gppFields);
                    responseList.add(response);
                    if (!gppFields.isEmpty()) {
                        if (trueCount > 0) {
                            gppJson10Matched.add(trueCount);
                        }
                        if (falseCount > 0) {
                            gppJson10NotMatched.add(falseCount);
                        }
                        if (nullCount > 0) {
                            gppJson10Null.add(nullCount);
                        }
                    }
                }
            }
            
            // Wait for ServiceClass2 response and update validateDTO with results
            List<GppJson28FieldValidationResponse> gppJson28Responses = gppJson28ResponseFuture.join();
            // Return responseList immediately
            executorService.execute(() -> {
                try {
                    // Convert responseList to JSON string
                    String validatedJson = convertResponseListToJson(responseList);

                    // Update validated_json in database
                    projectSOTDao.updateValidationJson(validatedJson, uid);
                } catch (Exception e) {
                    // Handle any exceptions here
                    e.printStackTrace();
                }
            });

            return responseList;
        } catch (IOException e) {
            throw new RuntimeException("Error processing JSON data", e);
        }
    }

    public  String convertResponseListToJson(List<GppFieldValidationResponse> responseList) {
        try {
            return objectMapper.writeValueAsString(responseList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting responseList to JSON", e);
        }
    }
	private List<Map<String, Object>> processSotJson(List<Map<String, Object>> sotJsonList) {
        List<Map<String, Object>> processedList = new ArrayList<>();

        for (Map<String, Object> sotRecord : sotJsonList) {
            Map<String, Object> processedRecord = new HashMap<>(sotRecord);

            for (Map.Entry<String, Object> entry : processedRecord.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString().trim(); // Trim white spaces

                // Check for replacements
                if (replacementProperties.containsKey(value)) {
                    value = replacementProperties.getProperty(value);
                }

                // Process date format
                if (isDate(value)) {
                    value = reformatDate(value);
                }

                processedRecord.put(key, value);
            }

            processedList.add(processedRecord);
        }

        return processedList;
    }

    private boolean isDate(String value) {
        // Simple check for date format, can be improved with regex or other date validation logic
        return value.matches("\\d{1,2}/\\d{1,2}/\\d{2,4}");
    }

    private String reformatDate(String date) throws ParseException {
        String[] possibleDateFormats = {"dd/MM/yyyy", "d/M/yy", "dd/MM/yy"};

        for (String format : possibleDateFormats) {
            try {
                return "1" + new SimpleDateFormat("yyMMdd").format(new SimpleDateFormat(format).parse(date));
            } catch (ParseException e) {
                // Try the next format
            }
        }
        throw new ParseException("Date format not recognized: " + date, 0);
    }

    private List<Map<String, Object>> filterGppJsonList(List<Map<String, Object>> gppJsonList, String listName, String gppNetworkName) {
        return gppJsonList.stream()
                .filter(gppObject -> listName.equals(gppObject.get("LIST_NAME").toString().trim()) &&
                        gppNetworkName.equals(gppObject.get("NETWORK").toString().trim()))
                .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> parseJson(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    private Map<String, GppFieldValidationResponse.Json28FieldValidation> processGppJson28(List<Map<String, Object>> gppJson28List) {
        List<Map<String, Object>> filteredList = gppJson28List.stream()
            .filter(map -> "02".equals(map.get("G")))
            .collect(Collectors.toList());

        Map<String, Map<String, Object>> groupedData = groupDataByADAECD(filteredList);
        Map<String, GppFieldValidationResponse.Json28FieldValidation> validationResults = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : groupedData.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> record1 = entry.getValue();
            Map<String, Object> record2 = groupedData.get(key.replace("S", "R"));

            if (record2 != null) {
                validationResults.putAll(compareRecords(record1, record2));
            }
        }

        return validationResults;
    }

    private Map<String, Map<String, Object>> groupDataByADAECD(List<Map<String, Object>> data) {
        Map<String, Map<String, Object>> groupedData = new HashMap<>();

        for (Map<String, Object> record : data) {
            String adaecd = (String) record.get("ADAECD");
            if (adaecd != null) {
                String key = getComparableKey(adaecd.trim());
                if (key != null) {
                    groupedData.put(key, record);
                }
            }
        }

        return groupedData;
    }

    private String getComparableKey(String adaecd) {
        if (adaecd.endsWith("R") || adaecd.endsWith("RT") || adaecd.matches("R\\d+")) {
            return adaecd.replace("R", "S");
        } else if (adaecd.endsWith("S") || adaecd.endsWith("SP") || adaecd.matches("S\\d+")) {
            return adaecd;
        }
        return null; // Only consider R and S endings for validation
    }

    private Map<String, GppFieldValidationResponse.Json28FieldValidation> compareRecords(Map<String, Object> record1, Map<String, Object> record2) {
        Map<String, GppFieldValidationResponse.Json28FieldValidation> fieldValidations = new HashMap<>();

        for (Map.Entry<String, Object> entry : record1.entrySet()) {
            String field = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = record2.get(field);

            if ("ADAECD".equals(field)) {
                continue; // Skip ADAECD comparison here
            }

            GppFieldValidationResponse.Json28FieldValidation validation = new GppFieldValidationResponse.Json28FieldValidation();
            validation.setSName((String) record1.get("ADAECD"));
            validation.setSValue((String) record2.get("ADAECD"));

            if (value2 == null) {
                validation.setValidationStatus(null);
            } else if (value1.equals(value2)) {
                validation.setValidationStatus("valid");
            } else {
                validation.setValidationStatus("invalid");
            }

            fieldValidations.put(field, validation);
        }

        return fieldValidations;
    }
}
