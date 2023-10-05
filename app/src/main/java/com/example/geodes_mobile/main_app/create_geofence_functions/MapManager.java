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
    private Polygon outerGeofence;
    private Polygon innerGeofence;
    private Marker marker;

    public MapManager(Context context, MapView mapView) {
        Configuration.getInstance().setUserAgentValue(context.getPackageName());
        this.mapView = mapView;
        this.mapView.setTileSource(TileSourceFactory.MAPNIK);
    }

    public void addMarkerWithGeofences(double latitude, double longitude, double outerGeofenceRadius, double innerGeofenceRadius) {
        clearGeofencesAndMarker();

        GeoPoint markerPoint = new GeoPoint(latitude, longitude);

        outerGeofence = new Polygon();
        outerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, outerGeofenceRadius));
        outerGeofence.setFillColor(Color.argb(102, 154, 220, 241));
        outerGeofence.setStrokeColor(Color.rgb(80, 156, 180));
        outerGeofence.setStrokeWidth(3.0f);
        mapView.getOverlayManager().add(outerGeofence);

        innerGeofence = new Polygon();
        innerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, innerGeofenceRadius));
        innerGeofence.setFillColor(Color.argb(50, 0, 255, 0));
        innerGeofence.setStrokeColor(Color.rgb(91, 206, 137));
        innerGeofence.setStrokeWidth(3.0f);
        mapView.getOverlayManager().add(innerGeofence);

        marker = new Marker(mapView);
        marker.setPosition(markerPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null);
        mapView.getOverlays().add(marker);

        mapView.invalidate();
    }

    public void updateGeofences(GeoPoint markerPoint, double outerGeofenceRadius, double innerGeofenceRadius) {
        if (outerGeofence != null && innerGeofence != null && marker != null) {
            // Update outer geofence
            outerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, outerGeofenceRadius));

            // Update inner geofence
            innerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, innerGeofenceRadius));
            mapView.invalidate();
        }
    }

    private void clearGeofencesAndMarker() {
        mapView.getOverlayManager().remove(outerGeofence);
        mapView.getOverlayManager().remove(innerGeofence);
        mapView.getOverlays().remove(marker);

        mapView.invalidate();
    }
}
