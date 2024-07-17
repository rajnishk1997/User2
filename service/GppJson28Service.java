package com.optum.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.dao.GppJson28Dao;
import com.optum.dto.response.GppJson28FieldValidationResponse;
import com.optum.dto.response.GppJson28FieldValidationResponse.Json28FieldValidation;
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

	public GppJson28FieldValidationResponse comparePairs(List<Map<String, Object>> filteredList,
	        Set<String> distinct00001Values) {
	    GppJson28FieldValidationResponse response = new GppJson28FieldValidationResponse();

	    List<Map<String, Json28FieldValidation>> gppJson28FieldsList = new ArrayList<>();
	    int matchCount = 0;
	    int notMatchCount = 0;
	    int nullCount = 0;

	    for (String distinctValue : distinct00001Values) {
	        Map<String, Json28FieldValidation> gppJson28Fields = new HashMap<>();

	        for (Map<String, Object> gppJson : filteredList) {
	            if (gppJson.containsValue(distinctValue)) {
	                Map<String, Object> correspondingRow = findCorrespondingValue(gppJson, filteredList);

	                if (correspondingRow != null) {
	                    for (String key : gppJson.keySet()) {
	                        Object rValue = gppJson.get(key);
	                        Object sValue = correspondingRow.get(key);

	                        String validationStatusStr;
	                        if (Objects.toString(rValue, "").isEmpty() || Objects.toString(sValue, "").isEmpty()) {
	                            validationStatusStr = null;
	                            nullCount++;
	                        } else {
	                            boolean validationStatus = Objects.equals(rValue, sValue);
	                            validationStatusStr = validationStatus ? "true" : "false";
	                            if (validationStatus) {
	                                matchCount++;
	                            } else {
	                                notMatchCount++;
	                            }
	                        }

	                        gppJson28Fields.put(key, new Json28FieldValidation(validationStatusStr,
	                                Objects.toString(rValue, null), Objects.toString(sValue, null)));
	                    }
	                }
	            }
	        }

	        gppJson28FieldsList.add(gppJson28Fields);
	    }

	    response.setGppJson28Match(matchCount);
	    response.setGppJson28NotMatch(notMatchCount);
	    response.setGppJson28Null(nullCount);
	    response.setGppJson28Fields(gppJson28FieldsList);

	    executorService.execute(() -> {
	        try {
	            String validatedJson = objectMapper.writeValueAsString(response);
	            gppJson28Dao.updateValidationJson(validatedJson, uid);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    return response;
	}


	private Map<String, Object> findCorrespondingValue(Map<String, Object> rRow,
			List<Map<String, Object>> filteredList) {
		Map<String, Object> correspondingRow = null;
		String adbotxValue = (String) rRow.get("ADBOTX");
		String adaecdValue = (String) rRow.get("ADAECD");

		if (adbotxValue != null && adaecdValue != null) {
			// Perform the replacement logic based on your requirements
			String replacedAdbotxValue = replaceValues(adbotxValue);

			// Now check if there are other rows in filteredList with this replacedValue in
			// ADBOTX
			for (Map<String, Object> otherRow : filteredList) {
				if (otherRow.containsKey("ADBOTX") && otherRow.get("ADBOTX").equals(replacedAdbotxValue)) {
					String otherAdaecdValue = (String) otherRow.get("ADAECD");
					if (isPerfectMatch(adaecdValue, otherAdaecdValue)) {
						correspondingRow = otherRow;
						break; // Found the corresponding value, exit loop
					}
				}
			}

			// If no perfect match found with replaced ADBOTX, try replacing ADAECD suffixes
			if (correspondingRow == null) {
				String replacedAdaecdValue = replaceSuffix(adaecdValue);
				for (Map<String, Object> otherRow : filteredList) {
					String otherAdaecdValue = (String) otherRow.get("ADAECD");
					if (replacedAdaecdValue.equals(otherAdaecdValue)) {
						correspondingRow = otherRow;
						break; // Found the corresponding value, exit loop
					}
				}
			}
		}

		return correspondingRow;
	}

	// Helper method to determine if two ADAECD values are a perfect match
	private boolean isPerfectMatch(String adaecdValue, String otherAdaecdValue) {
		return adaecdValue.endsWith("R") && otherAdaecdValue.endsWith("S")
				&& adaecdValue.substring(0, adaecdValue.length() - 1)
						.equals(otherAdaecdValue.substring(0, otherAdaecdValue.length() - 1));
	}

	// Helper method to replace suffixes in ADAECD values
	private String replaceSuffix(String value) {
		// Regular expression to replace "R" at the end of the string with "S"
		String replacedValue = value.replaceAll("R$", "S").replaceAll("RT$", "SP").replaceAll("R(\\d+)$", "S$1");

// If no replacement was made and the value does not end with "S" or "SP", add " SP"
		if (replacedValue.equals(value) && !replacedValue.endsWith("S") && !replacedValue.endsWith("SP")) {
			replacedValue += " SP";
		}

		return replacedValue;

	}

	private String replaceValues(String adbotxValue) {
		if (adbotxValue == null || adbotxValue.isEmpty()) {
			return adbotxValue;
		}

		// Replace "RT" with "SP" if it appears at the end
		if (adbotxValue.endsWith("RT")) {
			adbotxValue = adbotxValue.substring(0, adbotxValue.length() - 2) + "SP";
		}

		// Replace "R" with "S" if it appears at the end
		if (adbotxValue.endsWith("R")) {
			adbotxValue = adbotxValue.substring(0, adbotxValue.length() - 1) + "S";
		}

		// Replace "1R" with "1S" if it appears at the end
		if (adbotxValue.endsWith("1R")) {
			adbotxValue = adbotxValue.substring(0, adbotxValue.length() - 2) + "1S";
		}

		// Replace "R2" with "S2" if it appears at the end
		if (adbotxValue.matches(".*R\\d$")) {
			adbotxValue = adbotxValue.replaceFirst("R(\\d)$", "S$1");
		}

		return adbotxValue;
	}
}
