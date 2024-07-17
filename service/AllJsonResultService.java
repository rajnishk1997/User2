package com.optum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.ProjectSOTDao;
import com.optum.dao.ReqRes;
import com.optum.entity.ResponseWrapper;

@Service
public class AllJsonResultService {

    private final ProjectSOTDao projectSOTDao;

    @Autowired
    public AllJsonResultService(ProjectSOTDao projectSOTDao) {
        this.projectSOTDao = projectSOTDao;
    }

    public ResponseWrapper<JsonNode> fetchJsonData(int number, String uid) {
        switch (number) {
            case 4:
                // Fetch and parse validated_json4 based on sotgppid_mapping = 8
                String json4String = projectSOTDao.findValidatedJson4BySotGppIdMapping(uid, 8);
                JsonNode json4 = parseJson(json4String); // Parse JSON string
                return new ResponseWrapper<>(json4, new ReqRes()); // Assuming ReqRes constructor or setter exists
            case 28:
                // Fetch and parse validated_json28 based on sotgppid_mapping = 8
                String json28String = projectSOTDao.findValidatedJson28BySotGppIdMapping(uid, 8);
                JsonNode json28 = parseJson(json28String); // Parse JSON string
                return new ResponseWrapper<>(json28, new ReqRes()); // Assuming ReqRes constructor or setter exists
            case 10:
                // Fetch and parse validated_json based on sotgppid_mapping = 8
                String jsonString = projectSOTDao.findValidatedJsonBySotGppIdMapping(uid, 8);
                JsonNode json = parseJson(jsonString); // Parse JSON string
                return new ResponseWrapper<>(json, new ReqRes()); // Assuming ReqRes constructor or setter exists
            default:
                throw new IllegalArgumentException("Invalid number parameter. Supported values: 4, 28, 10.");
        }
    }

    private JsonNode parseJson(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON: " + e.getMessage(), e);
        }
    }
}

