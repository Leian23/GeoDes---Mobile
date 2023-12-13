package com.example.geodes_mobile.main_app.bottom_sheet_content.schedules_section;

import java.util.List;

public class DataModel2 {
    private String schedTitle;
    private int schedImage;
    private int calendarImage;
    private String time;
    private String repeatTime;
    private int alarmIcon;
    private List<String> selectedItemsIds;

    private String AlertList;

    private String SchedId;

    public DataModel2(String schedTitle, int schedImage, int calendarImage, String time, String repeatTime, int alarmIcon, List<String> selectedItemsIds, String AlertLists, String SchedId) {
        this.schedTitle = schedTitle;
        this.schedImage = schedImage;
        this.calendarImage = calendarImage;
        this.time = time;
        this.repeatTime = repeatTime;
        this.alarmIcon = alarmIcon;
        this.selectedItemsIds = selectedItemsIds;
        this.AlertList = AlertLists;
        this.SchedId = SchedId;
    }

    public String getSchedTitle() {
        return schedTitle;
    }

    public int getSchedImage() {
        return schedImage;
    }

    public int getCalendarImage() {
        return calendarImage;
    }

    public String getTime() {
        return time;
    }

    public String getRepeatTime() {
        return repeatTime;
    }

    public int getAlarmIcon() {
        return alarmIcon;
    }

    public List<String> getSelectedItemsIds() {
        return selectedItemsIds;
    }

    public String getAlertList() {
        return AlertList;
    }

    public String getSchedId() {
        return SchedId;
    }
}
