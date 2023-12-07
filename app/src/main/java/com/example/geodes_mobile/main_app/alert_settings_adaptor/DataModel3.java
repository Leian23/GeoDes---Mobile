package com.example.geodes_mobile.main_app.alert_settings_adaptor;

public class DataModel3 {
    private String alertName;
    private String notesAlert;
    private String distance;
    private Boolean alertEnabled;
    private String unid;

    private int setAlertStat;

    private Double latitude;

    private Double longitude;

    private Float outerRadius;

    private Float innerRadius;

    private Boolean EntryType;

    private String innerCode;

    private String outerCode;




    public DataModel3(String alertName, String notesAlert, String distance, Boolean alertEnabled, String unid, int setAlertStat, Double latitude, Double longitude, Float outerRadius, Float innerRadius, Boolean EntryType, String innerCode, String outerCode) {
        this.alertName = alertName;
        this.notesAlert = notesAlert;
        this.distance = distance;
        this.alertEnabled = alertEnabled;
        this.unid = unid;
        this.setAlertStat = setAlertStat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.EntryType = EntryType;
        this.innerCode = innerCode;
        this.outerCode = outerCode;


    }

    public String getAlertName() {
        return alertName;
    }

    public String getNotesAlert() {
        return notesAlert;
    }

    public String getDistance() {
        return distance;
    }

    public Boolean getAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(Boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public String getId() { return unid;}

    public int getSetAlertStat() { return setAlertStat;}

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public float getInnerRadius() {return innerRadius;}

    public Boolean getEntryType() {
        return EntryType;
    }

    public String getInnerCode() {
        return innerCode;
    }
    public String getOuterCode() {
        return outerCode;
    }

}