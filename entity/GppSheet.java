package com.optum.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rx_gpp_sheet")
public class GppSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gppsheet_rid")
    private Long gppSheetRid;

    @Column(name = "gpp_sheet_name", unique = true)
    private String gppSheetName;

    @Column(name = "is_active")
    private boolean isActive;
    
    @Column(name = "rx_created_by")
    private Integer createdBy;

    @Column(name = "rx_modified_by")
    private Integer modifiedBy;

    @Column(name = "rx_create_datetime")
    private Date createdDate;

    @Column(name = "rx_modify_datetime")
    private Date modifiedDate;

    // Constructors, getters, and setters

    public GppSheet() {
    }

    public GppSheet(String gppSheetName, boolean isActive) {
        this.gppSheetName = gppSheetName;
        this.isActive = isActive;
    }

    // Getters and setters
    public Long getGppSheetRid() {
        return gppSheetRid;
    }

    public void setGppSheetRid(Long gppSheetRid) {
        this.gppSheetRid = gppSheetRid;
    }

    public String getGppSheetName() {
        return gppSheetName;
    }

    public void setGppSheetName(String gppSheetName) {
        this.gppSheetName = gppSheetName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

