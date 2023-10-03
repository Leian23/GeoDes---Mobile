// MapManager.java
package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.content.Context;
import android.graphics.Color;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

public class MapManager {
    private MapView mapView;

    public MapManager(Context context, MapView mapView) {
        Configuration.getInstance().setUserAgentValue(context.getPackageName());
        this.mapView = mapView;
        this.mapView.setTileSource(TileSourceFactory.MAPNIK);
    }

    public void addMarkerWithGeofences(double latitude, double longitude, double outerGeofenceRadius, double innerGeofenceRadius) {
        // Add the outer geofence around the marker
        GeoPoint markerPoint = new GeoPoint(latitude, longitude);

        // Add outer geofence
        Polygon outerGeofence = new Polygon();
        outerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, outerGeofenceRadius));
        outerGeofence.setFillColor(Color.argb(50, 0, 255, 0)); // Adjust alpha (50) as needed
        outerGeofence.setStrokeColor(Color.BLACK);
        outerGeofence.setStrokeWidth(2.0f);
        mapView.getOverlayManager().add(outerGeofence);

        // Add the inner geofence within the outer geofence
        Polygon innerGeofence = new Polygon();
        innerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, innerGeofenceRadius));
        innerGeofence.setFillColor(Color.argb(50, 255, 0, 0)); // Adjust alpha (50) as needed
        innerGeofence.setStrokeColor(Color.BLACK);
        innerGeofence.setStrokeWidth(2.0f);
        mapView.getOverlayManager().add(innerGeofence);

        // Add a marker to the map (last added, drawn on top)
        Marker marker = new Marker(mapView);
        marker.setPosition(markerPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Set anchor to bottom center
        marker.setInfoWindow(null);
        mapView.getOverlays().add(marker);

    }
}
