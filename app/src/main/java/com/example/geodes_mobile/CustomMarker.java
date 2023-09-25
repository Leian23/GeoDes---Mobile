package com.example.geodes_mobile;
import org.osmdroid.util.GeoPoint;

public class CustomMarker {
    private GeoPoint geoPoint;
    private String title;

    public CustomMarker(GeoPoint geoPoint, String title) {
        this.geoPoint = geoPoint;
        this.title = title;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public String getTitle() {
        return title;
    }
}
