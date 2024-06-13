package com.optum.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rx_sot_gpp_network_name")
public class SOTNetworkMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_rid")
    private int sRid;

    @Column(name = "s_sot_network_name")
    private String sSotNetworkName;

    @Column(name = "s_gpp_network_name")
    private String sGppNetworkName;

    @OneToOne
    @JoinColumn(name = "s_platform_id", referencedColumnName = "sp_rid")
    private SPlatform platform;

    @Column(name = "s_created_by")
    private Integer sCreatedBy;

    @Column(name = "s_modified_by")
    private Integer sModifiedBy;

    @Column(name = "s_modify_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sModifyDatetime;

    @Column(name = "s_created_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sCreatedDatetime;

    // Getters and Setters
    public int getsRid() {
        return sRid;
    }

    public void setsRid(int sRid) {
        this.sRid = sRid;
    }

    public String getsSotNetworkName() {
        return sSotNetworkName;
    }

    public void setsSotNetworkName(String sSotNetworkName) {
        this.sSotNetworkName = sSotNetworkName;
    }

    public String getsGppNetworkName() {
        return sGppNetworkName;
    }

    public void setsGppNetworkName(String sGppNetworkName) {
        this.sGppNetworkName = sGppNetworkName;
    }


    public SPlatform getPlatform() {
		return platform;
	}

	public void setPlatform(SPlatform platform) {
		this.platform = platform;
	}

	public Integer getsCreatedBy() {
        return sCreatedBy;
    }

    public void setsCreatedBy(Integer sCreatedBy) {
        this.sCreatedBy = sCreatedBy;
    }

    public Integer getsModifiedBy() {
        return sModifiedBy;
    }

    public void setsModifiedBy(Integer sModifiedBy) {
        this.sModifiedBy = sModifiedBy;
    }

    public Date getsModifyDatetime() {
        return sModifyDatetime;
    }

    public void setsModifyDatetime(Date sModifyDatetime) {
        this.sModifyDatetime = sModifyDatetime;
    }

    public Date getsCreatedDatetime() {
        return sCreatedDatetime;
    }

    public void setsCreatedDatetime(Date sCreatedDatetime) {
        this.sCreatedDatetime = sCreatedDatetime;
    }
}

