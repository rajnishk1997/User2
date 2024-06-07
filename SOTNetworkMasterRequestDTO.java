package com.optum.dto.request;

import com.optum.entity.SPlatform;

public class SOTNetworkMasterRequestDTO {

    private String sSotNetworkName;
    private String sGppNetworkName;
    private SPlatform sPlatform;
    private Integer currentUserId;

    // Constructors, getters, and setters

    public SOTNetworkMasterRequestDTO() {
    }

    public SOTNetworkMasterRequestDTO(String sSotNetworkName, String sGppNetworkName, SPlatform sPlatform, Integer currentUserId) {
        this.sSotNetworkName = sSotNetworkName;
        this.sGppNetworkName = sGppNetworkName;
        this.sPlatform = sPlatform;
        this.currentUserId = currentUserId;
    }

    public String getSSotNetworkName() {
        return sSotNetworkName;
    }

    public void setSSotNetworkName(String sSotNetworkName) {
        this.sSotNetworkName = sSotNetworkName;
    }

    public String getSGppNetworkName() {
        return sGppNetworkName;
    }

    public void setSGppNetworkName(String sGppNetworkName) {
        this.sGppNetworkName = sGppNetworkName;
    }

    public SPlatform getSPlatform() {
        return sPlatform;
    }

    public void setSPlatform(SPlatform sPlatform) {
        this.sPlatform = sPlatform;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Integer currentUserId) {
        this.currentUserId = currentUserId;
    }
}

