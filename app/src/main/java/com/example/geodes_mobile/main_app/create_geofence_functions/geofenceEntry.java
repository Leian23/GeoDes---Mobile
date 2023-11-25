package com.example.geodes_mobile.main_app.create_geofence_functions;

public class geofenceEntry {
    private String requestId;
    private double latitude;
    private double longitude;
    private float radius;
    private String geofenceName;
    private boolean addEntryGeofence;

    // Required default constructor for Firebase
    public geofenceEntry() {
    }

    public geofenceEntry(String requestId, double latitude, double longitude, float radius, String geofenceName, boolean addEntryGeofence) {
        this.requestId = requestId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.geofenceName = geofenceName;
        this.addEntryGeofence = addEntryGeofence;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getGeofenceName() {
        return geofenceName;
    }

    public void setGeofenceName(String geofenceName) {
        this.geofenceName = geofenceName;
    }

    public boolean isAddEntryGeofence() {
        return addEntryGeofence;
    }

    public void setAddEntryGeofence(boolean addEntryGeofence) {
        this.addEntryGeofence = addEntryGeofence;
    }
}
