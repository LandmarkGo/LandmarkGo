package com.OPSC.OPSC7312;

public class LocationClass
{
    public LocationClass(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public LocationClass(){}

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    Double latitude;
    Double longitude;
}
