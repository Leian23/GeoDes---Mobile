// DataModel3.java

package com.example.geodes_mobile.main_app.alert_settings_adaptor;

public class DataModel3 {
    private String schedTitle;
    private String distance;
    private String note;
    private int iconCal;
    private int entryImage;
    private int iconMarker;
    private boolean isAlertSwitchOn;
    // Add other fields as needed

    public DataModel3(String schedTitle, String distance, String note, int iconCal, int entryImage, int iconMarker, boolean isAlertSwitchOn) {
        this.schedTitle = schedTitle;
        this.distance = distance;
        this.note = note;
        this.iconCal = iconCal;
        this.entryImage = entryImage;
        this.iconMarker = iconMarker;
        this.isAlertSwitchOn = isAlertSwitchOn;
        // Initialize other fields as needed
    }

    public String getSchedTitle() {
        return schedTitle;
    }

    public String getDistance() {
        return distance;
    }

    public String getNote() {
        return note;
    }

    public int getIconCal() {
        return iconCal;
    }

    public int getEntryImage() {
        return entryImage;
    }

    public int getIconMarker() {
        return iconMarker;
    }

    public boolean isAlertSwitchOn() {
        return isAlertSwitchOn;
    }

    // Add getters and setters for other fields
}
