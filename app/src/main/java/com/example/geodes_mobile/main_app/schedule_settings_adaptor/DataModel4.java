// DataModel4.java

package com.example.geodes_mobile.main_app.schedule_settings_adaptor;

public class DataModel4 {
    private String schedTitle;
    private String clock;
    private String schedAlarms;
    private int iconCal;
    private int entryImage;
    private int iconMarker;
    private boolean isAlertSwitchOn;
    // Add other fields as needed

    public DataModel4(String schedTitle, String clock, String schedAlarms, int iconCal, int entryImage, int iconMarker, boolean isAlertSwitchOn) {
        this.schedTitle = schedTitle;
        this.clock = clock;
        this.schedAlarms = schedAlarms;
        this.iconCal = iconCal;
        this.entryImage = entryImage;
        this.iconMarker = iconMarker;
        this.isAlertSwitchOn = isAlertSwitchOn;
        // Initialize other fields as needed
    }

    public String getSchedTitle() {
        return schedTitle;
    }

    public String getDistance() {return clock;}

    public String getNote() {
        return schedAlarms;
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
