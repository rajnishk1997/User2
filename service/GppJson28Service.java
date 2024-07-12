package com.optum.service;

import com.optum.dao.GppJson28Dao;
import com.optum.dao.ReqRes;
import com.optum.dto.response.GppJson28FieldValidationResponse;
import com.optum.dto.response.GppJson28FieldValidationResponse.Json28FieldValidation;
import com.optum.entity.ResponseWrapper;
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
            
            return comparePairs(filteredList, distinct00001Values);
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

        private List<GppJson28FieldValidationResponse> comparePairs(List<Map<String, Object>> filteredList, Set<String> distinct00001Values) {
            List<GppJson28FieldValidationResponse> responses = new ArrayList<>();

            for (String value : distinct00001Values) {
                List<Map<String, Object>> matchingEntries = findMatchingEntries(filteredList, value);
                
                for (Map<String, Object> entry : matchingEntries) {
                	 for (Map<String, Object> pairedEntry : matchingEntries) {
                         GppJson28FieldValidationResponse response = compareFields(entry, pairedEntry);
                         responses.add(response);
                     }
                }
            }

            return responses;
        }

        private List<Map<String, Object>> findMatchingEntries(List<Map<String, Object>> gppJson28List, String value) {
            List<Map<String, Object>> matchedEntries = new ArrayList<>();

            // Step 1: Find the entry with ADAECD equal to value
            Map<String, Object> targetEntry = gppJson28List.stream()
                    .filter(item -> value.equals(item.get("ADAECD")))
                    .findFirst()
                    .orElse(null);

            if (targetEntry == null) {
                return matchedEntries; // No matching entry found
            }

            String targetAdbotx = (String) targetEntry.get("ADBOTX");

            // Step 2: Generate the possible matching ADBOTX values
            List<String> possibleAdbotxValues = generatePossibleAdbotxValues(targetAdbotx);

            // Step 3: Find the entries with matching ADBOTX values
            List<Map<String, Object>> potentialMatches = gppJson28List.stream()
                    .filter(item -> possibleAdbotxValues.contains(item.get("ADBOTX")))
                    .collect(Collectors.toList());

            // Step 4: Narrow down to the exact match by checking ADAECD
            for (Map<String, Object> entry : potentialMatches) {
                String entryAdbotx = (String) entry.get("ADBOTX");
                if (entryAdbotx.equals(targetAdbotx.replace("R", "S"))) {
                    matchedEntries.add(entry);
                } else if (entryAdbotx.equals(targetAdbotx.replace("RT", "SP"))) {
                    matchedEntries.add(entry);
                } else if (entryAdbotx.equals(targetAdbotx.replace("R", "SP"))) {
                    matchedEntries.add(entry);
                } // Add more conditions as needed
            }

            return matchedEntries;
        }

        private List<String> generatePossibleAdbotxValues(String adbotx) {
            List<String> possibleValues = new ArrayList<>();
            if (adbotx.endsWith("R")) {
                possibleValues.add(adbotx.replace("R", "S"));
            }
            if (adbotx.endsWith("RT")) {
                possibleValues.add(adbotx.replace("RT", "SP"));
            }
            if (adbotx.endsWith("R")) {
                possibleValues.add(adbotx.replace("R", "SP"));
            }
            // Add more rules as needed
            return possibleValues;
        }
      
        private GppJson28FieldValidationResponse compareFields(Map<String, Object> entry, Map<String, Object> pairedEntry) {
            GppJson28FieldValidationResponse response = new GppJson28FieldValidationResponse();
            Map<String, GppJson28FieldValidationResponse.Json28FieldValidation> validationMap = new HashMap<>();

            Set<String> allKeys = new HashSet<>();
            allKeys.addAll(entry.keySet());
            allKeys.addAll(pairedEntry.keySet());

            for (String key : allKeys) {
                String entryValue = entry.containsKey(key) ? String.valueOf(entry.get(key)) : "";
                String pairedEntryValue = pairedEntry.containsKey(key) ? String.valueOf(pairedEntry.get(key)) : "";
                String validationStatus;

                if (entryValue.equals(pairedEntryValue)) {
                    validationStatus = "true";
                } else if (entryValue.isEmpty() || pairedEntryValue.isEmpty()) {
                    validationStatus = "null";
                } else {
                    validationStatus = "false";
                }

                GppJson28FieldValidationResponse.Json28FieldValidation fieldValidation = new GppJson28FieldValidationResponse.Json28FieldValidation();
                fieldValidation.setValidationStatus(validationStatus);
                fieldValidation.setsValue(entryValue);
                fieldValidation.setrValue(pairedEntryValue);

                validationMap.put(key, fieldValidation);
            }

            response.setGppJson28Fields(validationMap);
            return response;
        }

    
}