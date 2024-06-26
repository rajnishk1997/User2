package com.optum.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rx_gpp_field_details")
public class GppFieldDetails {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "gpp_rid")
	    private int gppRid;
	 
	    @Column(name = "rx_gpp_field_name")
	    private String gppFieldName;
	  
	    @Column(name = "rx_gpp_field_rename")
	    private String gppFieldRename;
	    
	    @Column(name = "rx_gpp_validation_req")
	    private boolean isGPPValidationRequired;
	    
	    @Column(name = "rx_created_by")
	    private Integer createdBy;

	    @Column(name = "rx_modified_by")
	    private Integer modifiedBy;

	    @Column(name = "rx_create_datetime")
	    private Date createdDate;

	    @Column(name = "rx_modify_datetime")
	    private Date modifiedDate;

		public int getGppRid() {
			return gppRid;
		}

		public void setGppRid(int gppRid) {
			this.gppRid = gppRid;
		}

		public String getGppFieldName() {
			return gppFieldName;
		}

		public void setGppFieldName(String gppFieldName) {
			this.gppFieldName = gppFieldName;
		}

		public String getGppFieldRename() {
			return gppFieldRename;
		}

		public void setGppFieldRename(String gppFieldRename) {
			this.gppFieldRename = gppFieldRename;
		}

		public boolean isGPPValidationRequired() {
			return isGPPValidationRequired;
		}

		public void setGPPValidationRequired(boolean isGPPValidationRequired) {
			this.isGPPValidationRequired = isGPPValidationRequired;
		}

		public Integer getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(Integer createdBy) {
			this.createdBy = createdBy;
		}

		public Integer getModifiedBy() {
			return modifiedBy;
		}

		public void setModifiedBy(Integer modifiedBy) {
			this.modifiedBy = modifiedBy;
		}

		public Date getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}

		public Date getModifiedDate() {
			return modifiedDate;
		}

		public void setModifiedDate(Date modifiedDate) {
			this.modifiedDate = modifiedDate;
		}
	    
}
