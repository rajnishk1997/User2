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

    @PersistenceContext
    private EntityManager entityManager;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Properties replacementProperties;

    public ComparisonService() throws IOException {
        Resource resource = new ClassPathResource("sot-replacements.properties");
        replacementProperties = PropertiesLoaderUtils.loadProperties(resource);
    }

    public List<GppFieldValidationResponse> compareSotAndGppJson(int uid) {
        try {
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

            // Process SOT JSON data
            List<Map<String, Object>> processedSotJsonList = processSotJson(sotJsonList);

            // Fetch mappings from the database
            List<SotGppRenameFieldsMapping> mappings = mappingRepository.findAll();

            // Prepare the response list
            List<GppFieldValidationResponse> responseList = new ArrayList<>();

            // Iterate over each processed SOT JSON dataRecord
            for (Map<String, Object> sotRecord : processedSotJsonList) {
                String listName = (String) sotRecord.get("LIST_NAME");
                String sotNetwork = (String) sotRecord.get("NETWORK");

                // Get corresponding GPP network name from SOTNetworkMaster
                SOTNetworkMaster networkMapping = networkMappingRepository.findBySotNetworkName(sotNetwork);
                String gppNetworkName = networkMapping.getsGppNetworkName();

                // Replace NETWORK field in processedSotJsonList with gppNetworkName
                sotRecord.put("NETWORK", gppNetworkName);

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
                                Object sotValue = sotRecord.get(sotFieldRename);
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
}
