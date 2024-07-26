package com.optum.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.optum.dto.GppJson28Field;

public class GppJson28FieldValidationResponse {

	private int gppJson28Match;
	private int gppJson28NotMatch;
	private int gppJson28Null;
	 @JsonProperty("gppJson28Fields")
	    private List<GppJson28Field> gppJson28Fields;
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
	public List<GppJson28Field> getGppJson28Fields() {
		return gppJson28Fields;
	}
	public void setGppJson28Fields(List<GppJson28Field> gppJson28Fields) {
		this.gppJson28Fields = gppJson28Fields;
	}
	 
}
