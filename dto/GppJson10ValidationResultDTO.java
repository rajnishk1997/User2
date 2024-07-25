package com.optum.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class GppJson10ValidationResultDTO {
	 private long ADA1DT;
	    private String ADAECD;
	    private long ADAKDT;
	    private String ADBOTX;
	    private String CCOIHJ;
	    private String DESCRIPTION;
	    private String G;
	    private String K3FYVD;
	    private String NA_01;
	    private boolean VALIDATION;

    public GppJson10ValidationResultDTO(JsonNode node) {
    	  this.ADA1DT = node.get("ADA1DT").asLong();
	        this.ADAECD = node.get("ADAECD").asText();
	        this.ADAKDT = node.get("ADAKDT").asLong();
	        this.ADBOTX = node.get("ADBOTX").asText();
	        this.CCOIHJ = node.get("CCOIHJ").asText();
	        this.DESCRIPTION = node.get("DESCRIPTION").asText();
	        this.G = node.get("G").asText();
	        this.K3FYVD = node.get("K3FYVD").asText();
	        this.NA_01 = node.get("NA_01").asText();
	}

    

	public void setValidation(boolean validation) {
        this.VALIDATION = validation;
    }
}
