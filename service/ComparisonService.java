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
            Query query = entityManager.createNativeQuery("SELECT gpp_json4, sot_json FROM rxcl_audit.rx_sot_gpp_data WHERE uid =" + uid);
            List<Object[]> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                throw new RuntimeException("Data not found for UID: " + uid);
            }

            Object[] jsonData = resultList.get(0);
            String sotJson = (String) jsonData[1];
            String gppJson = (String) jsonData[0];

            List<Map<String, Object>> sotJsonMap = objectMapper.readValue(sotJson,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            List<Map<String, Object>> dataRecords = (List<Map<String, Object>>) sotJsonMap.get("dataRecords");

            List<Map<String, Object>> gppJsonList = objectMapper.readValue(gppJson,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            // Process SOT JSON
            List<Map<String, Object>> processedSotJsonList = processSotJson(dataRecords);

            // Fetch mappings from database
            List<SotGppRenameFieldsMapping> mappings = mappingRepository.findAll();

            // Prepare response list
            List<GppFieldValidationResponse> responseList = new ArrayList<>();

            // Iterate over each dataRecord in processed SOT JSON
            for (Map<String, Object> sotRecord : processedSotJsonList) {
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

    private List<Map<String, Object>> processSotJson(List<Map<String, Object>> dataRecords) {
        List<Map<String, Object>> processedRecords = new ArrayList<>();
        for (Map<String, Object> record : dataRecords) {
            Map<String, Object> processedRecord = new HashMap<>();
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() != null ? entry.getValue().toString().trim() : "";

                // Check for replacements
                if (replacementProperties.containsKey(value)) {
                    value = replacementProperties.getProperty(value);
                } else if (isDate(value)) {
                    // Process date format only if not replaced
                    value = reformatDate(value);
                }

                processedRecord.put(key, value);
            }
            processedRecords.add(processedRecord);
        }
        return processedRecords;
    }

    private boolean isDate(String value) {
        return value.matches("\\d{1,2}/\\d{1,2}/\\d{2,4}");
    }

    private String reformatDate(String date) {
        List<String> formats = Arrays.asList("dd/MM/yyyy", "d/M/yy", "dd/MM/yy");
        for (String format : formats) {
            try {
                return new SimpleDateFormat("MM/dd/yy").format(new SimpleDateFormat(format).parse(date));
            } catch (ParseException ignored) {
            }
        }
        return date;
    }

    private List<Map<String, Object>> filterGppJsonList(List<Map<String, Object>> gppJsonList, String listName, String gppNetworkName) {
        return gppJsonList.stream()
                .filter(gppObject -> listName.equals(gppObject.get("LIST_NAME").toString().trim()) &&
                        gppNetworkName.equals(gppObject.get("NETWORK").toString().trim()))
                .collect(Collectors.toList());
    }
}
