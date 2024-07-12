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
                    GppJson28FieldValidationResponse response = compareFields(entry);
                    responses.add(response);
                }
            }

            return responses;
        }

        private List<Map<String, Object>> findMatchingEntries(List<Map<String, Object>> gppJson28List, String value) {
            return gppJson28List.stream()
                    .filter(item -> {
                        String adbotx = (String) item.get("ADBOTX");
                        return adbotx.endsWith("S") || adbotx.endsWith("SP");
                    })
                    .collect(Collectors.toList());
        }

        private GppJson28FieldValidationResponse compareFields(Map<String, Object> entry) {
            GppJson28FieldValidationResponse response = new GppJson28FieldValidationResponse();
            Map<String, GppJson28FieldValidationResponse.Json28FieldValidation> validationMap = new HashMap<>();

            Set<String> allKeys = new HashSet<>();
            allKeys.addAll(entry.keySet());

            for (String key : allKeys) {
                String entryValue = entry.containsKey(key) ? String.valueOf(entry.get(key)) : "";
                String validationStatus = "false";

                if (!entryValue.isEmpty()) {
                    validationStatus = "true";
                }

                GppJson28FieldValidationResponse.Json28FieldValidation fieldValidation = new GppJson28FieldValidationResponse.Json28FieldValidation();
                fieldValidation.setValidationStatus(validationStatus);
                fieldValidation.setsValue(key);
                fieldValidation.setrValue(entryValue);

                validationMap.put(key, fieldValidation);
            }

            response.setGppJson28Fields(validationMap);
            return response;
        }
    
}