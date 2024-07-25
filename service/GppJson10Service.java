package com.optum.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dto.GppJson10ValidationResultDTO;

@Service
public class GppJson10Service {

	 public CompletableFuture<List<GppJson10ValidationResultDTO>> validateCtrxFields(JsonNode ctrxFields, String uid) {
	        return CompletableFuture.supplyAsync(() -> {
	            List<GppJson10ValidationResultDTO> validationResults = new ArrayList<>();
	            try {
	                // Fetch gppJson10 from the database based on uid
	                String gppJson10 = fetchGppJson10FromDatabase(uid);
	                ObjectMapper mapper = new ObjectMapper();
	                JsonNode gppJsonNode = mapper.readTree(gppJson10);

	                if (ctrxFields.get("term").asBoolean()) {
	                    validateTermField(gppJsonNode, validationResults);
	                }
	                if (ctrxFields.get("add").asBoolean()) {
	                    int graceNumbers = ctrxFields.get("graceNumbers").asInt();
	                    validateAddField(gppJsonNode, graceNumbers, validationResults);
	                }
	            } catch (Exception e) {
	                // Handle exception
	                e.printStackTrace();
	            }
	            return validationResults;
	        });
	    }

	    private String fetchGppJson10FromDatabase(String uid) {
	        // Implement the database fetching logic here
	        return "[]"; // Return fetched gppJson10 as String
	    }

	    private void validateTermField(JsonNode gppJsonNode, List<GppJson10ValidationResultDTO> validationResults) {
	        for (JsonNode node : gppJsonNode) {
	            if ("CTRX75".equals(node.get("K3FYVD").asText().trim())) {
	            	GppJson10ValidationResultDTO dto = new GppJson10ValidationResultDTO(node);
	                dto.setValidation(false);
	                validationResults.add(dto);
	            }
	        }
	    }

	    private void validateAddField(JsonNode gppJsonNode, int graceNumbers, List<GppJson10ValidationResultDTO> validationResults) {
	        for (JsonNode node : gppJsonNode) {
	            if ("CTRX75".equals(node.get("K3FYVD").asText().trim())) {
	            	GppJson10ValidationResultDTO dto = new GppJson10ValidationResultDTO(node);
	                if (graceNumbers == node.get("G").asInt()) {
	                    dto.setValidation(true);
	                } else {
	                    dto.setValidation(false);
	                }
	                validationResults.add(dto);
	            }
	        }
	    }
}

