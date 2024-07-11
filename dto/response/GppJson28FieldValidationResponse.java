package com.optum.dto.response;


import java.util.Map;

public class GppJson28FieldValidationResponse {
    private Map<String, Json28FieldValidation> gppJson28Fields;

    public Map<String, Json28FieldValidation> getGppJson28Fields() {
        return gppJson28Fields;
    }

    public void setGppJson28Fields(Map<String, Json28FieldValidation> gppJson28Fields) {
        this.gppJson28Fields = gppJson28Fields;
    }

    public static class Json28FieldValidation {
        private String validationStatus;
        private String sName;
        private String sValue;

        // Getters and setters
        public String getValidationStatus() {
            return validationStatus;
        }

        public void setValidationStatus(String validationStatus) {
            this.validationStatus = validationStatus;
        }

        public String getSName() {
            return sName;
        }

        public void setSName(String sName) {
            this.sName = sName;
        }

        public String getSValue() {
            return sValue;
        }

        public void setSValue(String sValue) {
            this.sValue = sValue;
        }
    }
}
