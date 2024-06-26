package com.optum.entity;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "rx_sot_gpp_field_mapping")
public class SotGppFieldMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sot_gpp_rid")
    private int sotGppRid;

    @ManyToOne
    @JoinColumn(name = "sot_field_rename", referencedColumnName = "rx_sot_field_rename")
    private SotFieldDetails sotFieldDetails;

    @ManyToOne
    @JoinColumn(name = "gpp_field_rename", referencedColumnName = "rx_gpp_field_rename")
    private GppFieldDetails gppFieldDetails;
    
    @Column(name = "rx_created_by")
    private Integer createdBy;

    @Column(name = "rx_modified_by")
    private Integer modifiedBy;

    @Column(name = "rx_create_datetime")
    private Date createdDate;

    @Column(name = "rx_modify_datetime")
    private Date modifiedDate;

    // Constructors, getters, setters, etc.

    public int getSotGppRid() {
        return sotGppRid;
    }

    public void setSotGppRid(int sotGppRid) {
        this.sotGppRid = sotGppRid;
    }

    public SotFieldDetails getSotFieldDetails() {
        return sotFieldDetails;
    }

    public void setSotFieldDetails(SotFieldDetails sotFieldDetails) {
        this.sotFieldDetails = sotFieldDetails;
    }

    public GppFieldDetails getGppFieldDetails() {
        return gppFieldDetails;
    }

    public void setGppFieldDetails(GppFieldDetails gppFieldDetails) {
        this.gppFieldDetails = gppFieldDetails;
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

