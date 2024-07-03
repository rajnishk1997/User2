package com.optum.dto.response;

import java.util.Map;

public class GppFieldValidationResponse {
    private Map<String, FieldValidation> gppFields;

    public static class FieldValidation {
        private Object value;
        private String validationStatus; // "valid", "invalid", or null
        private Object expectedValue; // Only set when validationStatus is "invalid"
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		public String getValidationStatus() {
			return validationStatus;
		}
		public void setValidationStatus(String validationStatus) {
			this.validationStatus = validationStatus;
		}
		public Object getExpectedValue() {
			return expectedValue;
		}
		public void setExpectedValue(Object expectedValue) {
			this.expectedValue = expectedValue;
		}

        
    }

	public Map<String, FieldValidation> getGppFields() {
		return gppFields;
	}

	public void setGppFields(Map<String, FieldValidation> gppFields) {
		this.gppFields = gppFields;
	}
    
}
