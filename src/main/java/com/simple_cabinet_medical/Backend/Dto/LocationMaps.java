package com.simple_cabinet_medical.Backend.Dto;

import java.math.BigInteger;

public class LocationMaps {

    private BigInteger latitude;
    private BigInteger longitude;
    private String displayName;

    public LocationMaps(BigInteger latitude, BigInteger longitude, String displayName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.displayName = displayName;
    }

    public BigInteger getLatitude() {
        return latitude;
    }

    public void setLatitude(BigInteger latitude) {
        this.latitude = latitude;
    }

    public BigInteger getLongitude() {
        return longitude;
    }

    public void setLongitude(BigInteger longitude) {
        this.longitude = longitude;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
