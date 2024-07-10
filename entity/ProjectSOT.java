package com.optum.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="rx_project_sot")
public class ProjectSOT {

	@Id
	private long uid;
	
	 @Column(name = "validated_json", columnDefinition = "TEXT") 
	    private String validationJson;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getValidationJson() {
		return validationJson;
	}

	public void setValidationJson(String validationJson) {
		this.validationJson = validationJson;
	}
	
}
