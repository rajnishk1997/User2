package com.optum.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rx_sot_field_details")
public class SotFieldDetails {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "sot_rid")
	    private int sotRid;
	  
	    @Column(name = "rx_sot_field_name")
	    private String sotFieldName;
	  
	    @Column(name = "rx_sot_field_rename")
	    private String sotFieldRename;
	    
	    @Column(name = "rx_exact_match")
	    private String exactMatch;
	    
	    @Column(name = "rx_sot_validation_req")
	    private boolean isSOTValidationRequired;
	    
	    @Column(name = "rx_created_by")
	    private Integer createdBy;

	    @Column(name = "rx_modified_by")
	    private Integer modifiedBy;

	    @Column(name = "rx_create_datetime")
	    private Date createdDate;

	    @Column(name = "rx_modify_datetime")
	    private Date modifiedDate;

		public int getSotRid() {
			return sotRid;
		}

		public void setSotRid(int sotRid) {
			this.sotRid = sotRid;
		}

		public String getSotFieldName() {
			return sotFieldName;
		}

		public void setSotFieldName(String sotFieldName) {
			this.sotFieldName = sotFieldName;
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

		public String getSotFieldRename() {
			return sotFieldRename;
		}

		public void setSotFieldRename(String sotFieldRename) {
			this.sotFieldRename = sotFieldRename;
		}

		public boolean isSOTValidationRequired() {
			return isSOTValidationRequired;
		}

		public void setSOTValidationRequired(boolean isSOTValidationRequired) {
			this.isSOTValidationRequired = isSOTValidationRequired;
		}

		public String getExactMatch() {
			return exactMatch;
		}

		public void setExactMatch(String exactMatch) {
			this.exactMatch = exactMatch;
		}
	    
}
