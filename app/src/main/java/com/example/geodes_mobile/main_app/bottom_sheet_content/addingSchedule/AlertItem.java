package com.example.geodes_mobile.main_app.bottom_sheet_content.addingSchedule;

public class AlertItem {
    private String alertName;
    private String uniqueId;

    private int alertType;

    public AlertItem(String alertName, String uniqueId, int alertType) {
        this.alertName = alertName;
        this.uniqueId = uniqueId;
        this.alertType = alertType;
    }

    public String getAlertName() {
        return alertName;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public int getAlertType() { return alertType;}
}
