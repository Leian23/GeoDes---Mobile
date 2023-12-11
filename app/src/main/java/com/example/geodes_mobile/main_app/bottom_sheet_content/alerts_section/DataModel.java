package com.example.geodes_mobile.main_app.bottom_sheet_content.alerts_section;

import org.osmdroid.util.GeoPoint;

public class DataModel {
    private String distance;
    private int imageResource;
    private String alertTitle;
    private String note;
    private int imageResource1;
    private GeoPoint geoPoint; // New field to store GeoPoint

    private String unId;




    public DataModel( int imageResource, String alertTitle, String note, GeoPoint geoPoint, String unId) {

        this.imageResource = imageResource;
        this.alertTitle = alertTitle;
        this.note = note;
        this.geoPoint = geoPoint;
        this.unId = unId;
    }

    public String getDistance() {
        return distance;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public String getNote() {
        return note;
    }

    public int getImageResource1() {
        return imageResource1;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
    public String getUnId() {
        return unId;
    }



}
