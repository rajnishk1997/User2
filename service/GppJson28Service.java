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
        List<Map<String, Object>> gppJson28List = parseJson(gppJson28Str);
        List<Map<String, Object>> filteredList = filterByG(gppJson28List);
        return comparePairs(filteredList);
    }

    private List<Map<String, Object>> parseJson(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse gpp_json28", e);
        }
    }

    private List<Map<String, Object>> filterByG(List<Map<String, Object>> gppJson28List) {
        return gppJson28List.stream()
                .filter(item -> "02".equals(item.get("G")))
                .collect(Collectors.toList());
    }

    private List<GppJson28FieldValidationResponse> comparePairs(List<Map<String, Object>> filteredList) {
        Map<String, Map<String, Object>> rMap = new HashMap<>();
        Map<String, Map<String, Object>> sMap = new HashMap<>();

//      If rMap contains an entry with "ADAECD": "BRM26NJ R22", it will look for a corresponding entry in sMap where "ADAECD" ends with "S22".
//      Similarly, an entry with "ADAECD": "BRM26NJ RT" in rMap would be compared with "ADAECD" ending with "SP" in sMap.
        for (Map<String, Object> item : filteredList) {
            String adaecd = (String) item.get("ADAECD");
            if (adaecd.endsWith("R") || adaecd.endsWith("RT") || adaecd.endsWith("R2") || adaecd.endsWith("R3")) {
                rMap.put(adaecd, item);
            } else if (adaecd.endsWith("S") || adaecd.endsWith("SP") || adaecd.endsWith("S2") || adaecd.endsWith("S3")) {
                sMap.put(adaecd, item);
            } else if (adaecd.matches(".*R\\d+$")) {
                String sKey = adaecd.replaceFirst("R(\\d+)$", "S$1");
                sMap.put(sKey, item);
            } else if (adaecd.matches(".*S\\d+$")) {
                String rKey = adaecd.replaceFirst("S(\\d+)$", "R$1");
                rMap.put(rKey, item);
            }
        }

        List<GppJson28FieldValidationResponse> responses = new ArrayList<>();
//      It iterates over entries in rMap.
//      For each entry (rEntry), it determines the corresponding sKey in sMap by replacing "R" with "S" and "RT" with "SP" in rKey.
//      If sMap contains the sKey, it calls compareFields method to compare the JSON objects (rEntry.getValue() and sMap.get(sKey)).
        for (Map.Entry<String, Map<String, Object>> rEntry : rMap.entrySet()) {
            String rKey = rEntry.getKey();
            String sKey = rKey.replace("R", "S").replace("RT", "SP");
            if (sMap.containsKey(sKey)) {
                responses.add(compareFields(rEntry.getValue(), sMap.get(sKey)));
            }
        }

        return responses;
    }

    private GppJson28FieldValidationResponse compareFields(Map<String, Object> rFields, Map<String, Object> sFields) {
        GppJson28FieldValidationResponse response = new GppJson28FieldValidationResponse();
        Map<String, Json28FieldValidation> validationMap = new HashMap<>();

        for (Map.Entry<String, Object> rEntry : rFields.entrySet()) {
            String key = rEntry.getKey();
            String rValue = String.valueOf(rEntry.getValue());
            String sValue = sFields.containsKey(key) ? String.valueOf(sFields.get(key)) : null;
            String validationStatus = rValue.equals(sValue) ? "true" : "false";

            Json28FieldValidation validation = new Json28FieldValidation();
            validation.setSName(key);
            validation.setSValue(sValue);
            validation.setValidationStatus(validationStatus);

            validationMap.put(key, validation);
        }

        response.setGppJson28Fields(validationMap);
        return response;
    }
}