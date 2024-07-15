package com.optum.service;

import com.optum.dao.GppJson28Dao;
import com.optum.dto.response.GppJson28FieldValidationResponse;
import com.optum.dto.response.GppJson28FieldValidationResponse.Json28FieldValidation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GppJson28Service {

    @Autowired
    private GppJson28Dao repository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<GppJson28FieldValidationResponse> getGppJson28ByUid(String uid) {
        String gppJson28Str = repository.findGppJson28ByUid(uid);
        String plancCodeOverrideStr = repository.findPlancCodeOverrideByUid(uid);

        List<Map<String, Object>> gppJson28List = parseJson(gppJson28Str);
        List<Map<String, Object>> plancCodeOverrideList = parseJson(plancCodeOverrideStr);

        Set<String> distinct00001Values = extractDistinct00001Values(plancCodeOverrideList);
        List<Map<String, Object>> filteredList = filterByG(gppJson28List);

        return validateGppJson28Fields(filteredList, distinct00001Values);
    }

    private List<Map<String, Object>> parseJson(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    private Set<String> extractDistinct00001Values(List<Map<String, Object>> plancCodeOverrideList) {
        return plancCodeOverrideList.stream()
                .map(item -> (String) item.get("00001"))
                .collect(Collectors.toSet());
    }

    private List<Map<String, Object>> filterByG(List<Map<String, Object>> gppJson28List) {
        return gppJson28List.stream()
                .filter(item -> "02".equals(item.get("G")))
                .collect(Collectors.toList());
    }

    private List<GppJson28FieldValidationResponse> validateGppJson28Fields(List<Map<String, Object>> filteredList, Set<String> distinct00001Values) {
        List<GppJson28FieldValidationResponse> responses = new ArrayList<>();

        for (String value : distinct00001Values) {
            List<Map<String, Object>> matchingEntries = findMatchingEntries(filteredList, value);

            if (matchingEntries.size() == 1) {
                Map<String, Object> entry = matchingEntries.get(0);
                GppJson28FieldValidationResponse response = createValidationResponse(entry);
                responses.add(response);
            } else if (matchingEntries.size() > 1) {
                Map<String, Object> matchedEntry = findMatchedEntry(matchingEntries, value);
                if (matchedEntry != null) {
                    GppJson28FieldValidationResponse response = createValidationResponse(matchedEntry);
                    responses.add(response);
                }
            }
        }

        return responses;
    }

    private List<Map<String, Object>> findMatchingEntries(List<Map<String, Object>> gppJson28List, String value) {
        return gppJson28List.stream()
                .filter(item -> value.equals(item.get("ADAECD")))
                .collect(Collectors.toList());
    }

    private Map<String, Object> findMatchedEntry(List<Map<String, Object>> matchingEntries, String value) {
        for (Map<String, Object> entry : matchingEntries) {
            String sValue = (String) entry.get("ADAECD");
            String matchedValue = sValue.replaceFirst("R$", "S");

            for (Map<String, Object> entryToMatch : matchingEntries) {
                if (matchedValue.equals(entryToMatch.get("ADAECD"))) {
                    return entryToMatch;
                }
            }
        }
        return null;
    }

    private GppJson28FieldValidationResponse createValidationResponse(Map<String, Object> entry) {
        GppJson28FieldValidationResponse response = new GppJson28FieldValidationResponse();
        Map<String, Json28FieldValidation> validationMap = new HashMap<>();

        for (String key : entry.keySet()) {
            Json28FieldValidation fieldValidation = new Json28FieldValidation();
            fieldValidation.setsValue(String.valueOf(entry.get(key)));

            // Determine rValue based on the rules for ADBOTX field
            String rValue = determineRValue(key, fieldValidation.getsValue());
            fieldValidation.setrValue(rValue);

            // Determine validation status
            String validationStatus = "true";
            if (!Objects.equals(fieldValidation.getsValue(), fieldValidation.getrValue())) {
                validationStatus = "false";
            } else if (fieldValidation.getsValue() == null || fieldValidation.getrValue() == null) {
                validationStatus = "null";
            }

            fieldValidation.setValidationStatus(validationStatus);
            validationMap.put(key, fieldValidation);
        }

        response.setGppJson28Fields(validationMap);
        return response;
    }

    private String determineRValue(String fieldName, String sValue) {
        if ("ADBOTX".equals(fieldName)) {
            // Implement your logic to determine rValue based on the rules for ADBOTX field
            // Example logic:
            if (sValue.endsWith("RT")) {
                return sValue.replace("RT", "SP");
            } else if (sValue.endsWith("R")) {
                return sValue.replace("R", "S");
            } else {
                return sValue; // Default case
            }
        }
        // Add more conditions for other fields if needed

        // Default case
        return sValue;
    }
}

