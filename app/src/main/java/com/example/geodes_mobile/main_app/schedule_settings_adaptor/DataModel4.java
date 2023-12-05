// DataModel4.java

package com.example.geodes_mobile.main_app.schedule_settings_adaptor;

import java.util.List;

public class DataModel4 {
    private String schedTitle;
    private String schedAlarms;
    private int iconCal;
    private int entryImage;
    private int iconMarker;
    private Boolean isAlertSwitchOn;

    private String getTime;

    private String uniqueId;

    private String Schedules;

    private List<String> selectedItemsIds;


    public DataModel4(String schedTitle, String getTime, String schedAlarms, int iconCal, int entryImage, int iconMarker, Boolean isAlertSwitchOn, String uniqueId, String Schedules, List<String> selectedItemsIds) {
        this.schedTitle = schedTitle;
        this.getTime = getTime;
        this.schedAlarms = schedAlarms;
        this.iconCal = iconCal;
        this.entryImage = entryImage;
        this.iconMarker = iconMarker;
        this.isAlertSwitchOn = isAlertSwitchOn;
        this.uniqueId = uniqueId;
        this.Schedules = Schedules;
        this.selectedItemsIds = selectedItemsIds;
    }

    public String getSchedTitle() {
        return schedTitle;
    }

    public String getTimeStart() {
        return getTime;
    }

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

    public void setAlertSwitchOn(boolean alertSwitchOn) {
        this.isAlertSwitchOn = alertSwitchOn;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getSchedules() {
        return Schedules;
    }

    public List<String> getSelectedItemsIds() {
        return selectedItemsIds;
    }
}

