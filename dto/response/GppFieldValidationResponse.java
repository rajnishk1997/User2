package com.optum.dto.response;

import java.util.*;

public class GppFieldValidationResponse {
	  private int gppJsonMatched;
	    private int gppJsonNotMatched;
	    private int gppJsonNull;
    private List<Map<String, FieldValidation>> gppFields;

    public static class FieldValidation {
        private Object gppValue;
        private String validationStatus; // "valid", "invalid", or null
        private Object sotRename; // Only set when validationStatus is "invalid"
        private Object sotValue; 
		

        
    }
    
    public static class Json28FieldValidation {
        private String validationStatus; // "valid", "invalid", or null
        private String sName; // ADAECD value
        private String sValue; // ADAECD value
		public String getValidationStatus() {
			return validationStatus;
		}
		public void setValidationStatus(String validationStatus) {
			this.validationStatus = validationStatus;
		}
		public String getsName() {
			return sName;
		}
		public void setsName(String sName) {
			this.sName = sName;
		}
		public String getsValue() {
			return sValue;
		}
		public void setsValue(String sValue) {
			this.sValue = sValue;
		}

        // Getters and setters
        
    }

    public List<Map<String, FieldValidation>> getGppFields() {
        return gppFields;
    }

    public void setGppFields(List<Map<String, FieldValidation>> gppFields) {
        this.gppFields = gppFields;
    }
    
}
