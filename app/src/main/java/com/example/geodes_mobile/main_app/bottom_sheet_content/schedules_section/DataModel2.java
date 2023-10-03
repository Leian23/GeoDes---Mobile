package com.example.geodes_mobile.main_app.bottom_sheet_content.schedules_section;
public class DataModel2 {
    private String schedTitle;
    private int schedImage;
    private int calendarImage;
    private String time;
    private String repeatTime;
    private int alarmIcon;
    private String alertLists;

    public DataModel2(String schedTitle, int schedImage, int calendarImage, String time, String repeatTime, int alarmIcon, String alertLists) {
        this.schedTitle = schedTitle;
        this.schedImage = schedImage;
        this.calendarImage = calendarImage;
        this.time = time;
        this.repeatTime = repeatTime;
        this.alarmIcon = alarmIcon;
        this.alertLists = alertLists;
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

    public String getAlertLists() {
        return alertLists;
    }
}
