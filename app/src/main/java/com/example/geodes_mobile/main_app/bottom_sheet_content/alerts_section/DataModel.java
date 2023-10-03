package com.example.geodes_mobile.main_app.bottom_sheet_content.alerts_section;

public class DataModel {
    private String distance;
    private int imageResource;
    private String alertTitle;
    private String note;
    private int imageResource1;

    public DataModel(String distance, int imageResource, String alertTitle, String note, int imageResource1) {
        this.distance = distance;
        this.imageResource = imageResource;
        this.alertTitle = alertTitle;
        this.note = note;
        this.imageResource1 = imageResource1; // Corrected line
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
}
