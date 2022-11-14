package com.OPSC.OPSC7312;

public class SettingsClass
{
    String uid;
    String measurement;
    String preferredLandmark;

    public SettingsClass()
    {

    }

    public SettingsClass(String uid, String measurement, String preferredLandmark) {
        this.uid = uid;
        this.measurement = measurement;
        this.preferredLandmark = preferredLandmark;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getPreferredLandmark() {
        return preferredLandmark;
    }

    public void setPreferredLandmark(String preferredLandmark) {
        this.preferredLandmark = preferredLandmark;
    }
}