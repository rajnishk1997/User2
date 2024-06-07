package com.optum.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "s_plateform")
public class SPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sp_rid")
    private int spRid;

    @Column(name = "s_platform_name")
    private String sPlatformName;

    @Column(name = "s_created_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sCreatedDatetime;

    // Getters and Setters
    public int getSpRid() {
        return spRid;
    }

    public void setSpRid(int spRid) {
        this.spRid = spRid;
    }

    public String getsPlatformName() {
        return sPlatformName;
    }

    public void setsPlatformName(String sPlatformName) {
        this.sPlatformName = sPlatformName;
    }

    public Date getsCreatedDatetime() {
        return sCreatedDatetime;
    }

    public void setsCreatedDatetime(Date sCreatedDatetime) {
        this.sCreatedDatetime = sCreatedDatetime;
    }
}

