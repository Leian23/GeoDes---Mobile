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

    private Double outerRadius;



    public DataModel3(String alertName, String notesAlert, String distance, Boolean alertEnabled, String unid, int setAlertStat, Double latitude, Double longitude, Double outerRadius) {
        this.alertName = alertName;
        this.notesAlert = notesAlert;
        this.distance = distance;
        this.alertEnabled = alertEnabled;
        this.unid = unid;
        this.setAlertStat = setAlertStat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.outerRadius = outerRadius;
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

    public Double getOuterRadius() {
        return outerRadius;
    }
}