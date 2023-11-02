package com.example.geodes_mobile.main_app.search_location;

public class LocationResult {
    private String name;
    private double latitude;
    private double longitude;

    public LocationResult(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
