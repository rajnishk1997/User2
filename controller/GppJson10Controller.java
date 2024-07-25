package com.optum.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.ReqRes;
import com.optum.dto.GppJson10DTO;
import com.optum.dto.GppJson10ValidationResultDTO;
import com.optum.entity.ResponseWrapper;
import com.optum.service.GppJson10Service;

@RestController
@RequestMapping("/gppJson10")
public class GppJson10Controller {

	@Autowired
    private GppJson10Service ctrxValidationService;

    @PostMapping("/validate/{uid}")
    public ResponseWrapper<List<GppJson10ValidationResultDTO>> validateCtrx(@PathVariable String uid) {
        String sotJson = getSotJson(uid); // Implement this method to fetch sotJson
        ObjectMapper mapper = new ObjectMapper();
        List<GppJson10ValidationResultDTO> validationResults = null;
        String message;

        try {
            JsonNode jsonNode = mapper.readTree(sotJson);
            JsonNode ctrxFields = jsonNode.get("ctrx");

            if (ctrxFields.get("ifCTRXAvailable").asBoolean()) {
                CompletableFuture<List<GppJson10ValidationResultDTO>> futureResults =
                        ctrxValidationService.validateCtrxFields(ctrxFields, uid);
                validationResults = futureResults.get(); // Wait for async execution
                message = "Validation completed successfully";
            } else {
                message = "No CTRX found";
            }
        } catch (Exception e) {
            message = "Error during validation: " + e.getMessage();
            e.printStackTrace();
        }

        return new ResponseWrapper<>(validationResults, new ReqRes(200, null, message));
    }

    private String getSotJson(String uid) {
        // Implement the logic to fetch sotJson from the database using uid
        return "{}"; // Return fetched sotJson as String
    }
}

