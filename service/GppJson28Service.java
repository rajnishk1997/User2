package com.optum.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.GppJson28Dao;
import com.optum.dto.GppJson28Field;
import com.optum.dto.response.GppJson28FieldValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Transactional
public class GppJson28Service {

	@Autowired
	private GppJson28Dao gppJson28Dao;

	@Autowired
	private ExecutorService executorService;

	@Autowired
	private ObjectMapper objectMapper;

	@Async
	public CompletableFuture<List<GppJson28FieldValidationResponse>> getGppJson28ByUidAsync(int uid) {
		return CompletableFuture.supplyAsync(() -> getGppJson28ByUid(uid), executorService);
	}

	public List<GppJson28FieldValidationResponse> getGppJson28ByUid(int uid) {
		String gppJson28Str = gppJson28Dao.findGppJson28ByUid(uid);
		String plancCodeOverrideStr = gppJson28Dao.findPlancCodeOverrideByUid(uid);

		List<Map<String, Object>> gppJson28List = parseJson(gppJson28Str);
		List<Map<String, Object>> plancCodeOverrideList = parseJson(plancCodeOverrideStr);

		Set<String> distinct00001Values = extractDistinct00001Values(plancCodeOverrideList);
		List<Map<String, Object>> filteredList = filterByG(gppJson28List);

		return comparePairs(filteredList, distinct00001Values);
	}

	private List<Map<String, Object>> parseJson(String jsonStr) {
		try {
			return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse JSON", e);
		}
	}

	private Set<String> extractDistinct00001Values(List<Map<String, Object>> plancCodeOverrideList) {
		Set<String> distinctValues = new HashSet<>();
		for (Map<String, Object> entry : plancCodeOverrideList) {
			if (entry.containsKey("00001")) {
				distinctValues.add(entry.get("00001").toString());
			}
		}
		return distinctValues;
	}

	private List<Map<String, Object>> filterByG(List<Map<String, Object>> gppJson28List) {
		List<Map<String, Object>> filteredList = new ArrayList<>();
		for (Map<String, Object> entry : gppJson28List) {
			if (entry.containsKey("G") && "02".equals(entry.get("G").toString())) {
				filteredList.add(entry);
			}
		}
		return filteredList;
	}

	public List<GppJson28FieldValidationResponse> comparePairs(
		    List<Map<String, Object>> filteredList, Set<String> distinct00001Values) {
		    
		    List<GppJson28Field> gppFields = new ArrayList<>();
		    int matchCount = 0;
		    int notMatchCount = 0;
		    
		    for (Map<String, Object> row : filteredList) {
		        GppJson28Field gppField = mapToGppJson28Field(row);
		        String adaecdValue = (String) row.get("ADAECD");
		        
		        if (distinct00001Values.contains(adaecdValue.replaceAll("\\s", ""))) {
		            GppJson28Field pairField = findPair(filteredList, adaecdValue, gppField);
		            
		            if (pairField != null) {
		                matchCount++;
		                validateFields(gppField, pairField);
		            } else {
		                notMatchCount++;
		                gppField.setFAILEDVALIDATION("No Pair Found");
		            }
		        } else {
		            notMatchCount++;
		            gppField.setFAILEDVALIDATION("No Match Found");
		        }
		        
		        gppFields.add(gppField);
		    }
		    
		    GppJson28FieldValidationResponse response = new GppJson28FieldValidationResponse();
		    response.setGppJson28Match(matchCount);
		    response.setGppJson28NotMatch(notMatchCount);
		    response.setGppJson28Null(0); // Assuming no null checks are required based on given details
		    response.setGppJson28Fields(gppFields);
		    
		    return Collections.singletonList(response);
		}

		private void validateFields(GppJson28Field field1, GppJson28Field field2) {
		    StringBuilder failedValidation = new StringBuilder();
		    if (!field1.getH().equals(field2.getH())) failedValidation.append("H,");
		    if (!field1.getI().equals(field2.getI())) failedValidation.append("I,");
		    if (!field1.getK().equals(field2.getK())) failedValidation.append("K,");
		    
		    String failedValidationStr = failedValidation.length() > 0
		        ? failedValidation.substring(0, failedValidation.length() - 1)
		        : "";

		    field1.setFAILEDVALIDATION(failedValidationStr);
		    field2.setFAILEDVALIDATION(failedValidationStr);
		}

		private GppJson28Field mapToGppJson28Field(Map<String, Object> row) {
		    GppJson28Field field = new GppJson28Field();
		    field.setADA1DT((String) row.get("ADA1DT"));
		    field.setF((String) row.get("F"));
		    field.setG((String) row.get("G"));
		    field.setH((String) row.get("H"));
		    field.setI((String) row.get("I"));
		    field.setADAKDT((String) row.get("ADAKDT"));
		    field.setJ((String) row.get("J"));
		    field.setK((String) row.get("K"));
		    field.setADBOTX((String) row.get("ADBOTX"));
		    field.setL((String) row.get("L"));
		    field.setADAECD((String) row.get("ADAECD"));
		    return field;
		}

		private GppJson28Field findPair(List<Map<String, Object>> filteredList, String adaecdValue, GppJson28Field gppField) {
		    String valueWithoutSpaces = adaecdValue.replaceAll("\\s", "");
		    String replacedValue;
		    
		    // Step (i): Replace last "R" with "S"
		    if (valueWithoutSpaces.endsWith("R")) {
		        replacedValue = valueWithoutSpaces.substring(0, valueWithoutSpaces.length() - 1) + "S";
		        GppJson28Field pair = findInList(filteredList, replacedValue);
		        if (pair != null) return pair;
		    }
		    
		    // Step (ii): Replace second last "R" with "S"
		    int secondLastRIndex = valueWithoutSpaces.lastIndexOf('R', valueWithoutSpaces.length() - 2);
		    if (secondLastRIndex != -1) {
		        replacedValue = valueWithoutSpaces.substring(0, secondLastRIndex) + "S" + valueWithoutSpaces.substring(secondLastRIndex + 1);
		        GppJson28Field pair = findInList(filteredList, replacedValue);
		        if (pair != null) return pair;
		    }
		    
		    // Step (iii): Replace last "RT" with "SP"
		    if (valueWithoutSpaces.endsWith("RT")) {
		        replacedValue = valueWithoutSpaces.substring(0, valueWithoutSpaces.length() - 2) + "SP";
		        GppJson28Field pair = findInList(filteredList, replacedValue);
		        if (pair != null) return pair;
		    }
		    
		    // Step (iv): Append "S" at the end
		    replacedValue = valueWithoutSpaces + "S";
		    return findInList(filteredList, replacedValue);
		}

		private GppJson28Field findInList(List<Map<String, Object>> list, String value) {
		    for (Map<String, Object> row : list) {
		        String rowValue = ((String) row.get("ADAECD")).replaceAll("\\s", "");
		        if (value.equals(rowValue)) {
		            return mapToGppJson28Field(row);
		        }
		    }
		    return null;
		}





}
