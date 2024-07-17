package com.optum.dto.response;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GppJson28FieldValidationResponse {
	
	 private int gppJson28Match;
	    private int gppJson28NotMatch;
	    private int gppJson28Null;
	    private Map<String, Map<String, Json28FieldValidation>> gppFields = new HashMap<>();
	    private Map<String, Map<String, Json28FieldValidation>> gppFieldsR = new HashMap<>();

	    public static class Json28FieldValidation {
	        private String validationStatus;
	        private String value;

        // Getters and setters
        public String getValidationStatus() {
            return validationStatus;
        }

        public void setValidationStatus(String validationStatus) {
            this.validationStatus = validationStatus;
        }

       

		public Json28FieldValidation(String validationStatus, String value) {
			super();
			this.validationStatus = validationStatus;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Json28FieldValidation() {
			// TODO Auto-generated constructor stub
		}


    }
	  

	    public int getGppJson28Match() {
			return gppJson28Match;
		}

		public void setGppJson28Match(int gppJson28Match) {
			this.gppJson28Match = gppJson28Match;
		}

		public int getGppJson28NotMatch() {
			return gppJson28NotMatch;
		}

		public void setGppJson28NotMatch(int gppJson28NotMatch) {
			this.gppJson28NotMatch = gppJson28NotMatch;
		}

		public int getGppJson28Null() {
			return gppJson28Null;
		}

		public void setGppJson28Null(int gppJson28Null) {
			this.gppJson28Null = gppJson28Null;
		}

		public Map<String, Map<String, Json28FieldValidation>> getGppFields() {
			return gppFields;
		}

		public void setGppFields(Map<String, Map<String, Json28FieldValidation>> gppFields) {
			this.gppFields = gppFields;
		}

		public Map<String, Map<String, Json28FieldValidation>> getGppFieldsR() {
			return gppFieldsR;
		}

		public void setGppFieldsR(Map<String, Map<String, Json28FieldValidation>> gppFieldsR) {
			this.gppFieldsR = gppFieldsR;
		}

		public GppJson28FieldValidationResponse(Map<String, Map<String, Json28FieldValidation>> gppFields,
				Map<String, Map<String, Json28FieldValidation>> gppFieldsR) {
			super();
			this.gppFields = gppFields;
			this.gppFieldsR = gppFieldsR;
		}

		public GppJson28FieldValidationResponse() {
			// TODO Auto-generated constructor stub
		}

}
