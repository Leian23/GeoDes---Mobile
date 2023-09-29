package com.example.geodes_mobile.main_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;


public class LocationHandler {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final long MIN_TIME_BETWEEN_UPDATES = 5000;
    private static final float MIN_DISTANCE_BETWEEN_UPDATES = 10;
    private boolean isFirstLocationUpdate = true;

    private Context context;
    private MapView mapView;
    private IMapController mapController;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationHandler(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;

        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        mapController = mapView.getController();
        mapController.setZoom(15.0);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                showToast("Tapped on the map at: " + geoPoint.getLatitude() + ", " + geoPoint.getLongitude());
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint geoPoint) {
                showToast("Long pressed on the map at: " + geoPoint.getLatitude() + ", " + geoPoint.getLongitude());
                return false;
            }
        });
        mapView.getOverlays().add(0, mapEventsOverlay);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                initializeLocationUpdates();
            }
        } else {
            initializeLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        // Unregister the location listener to stop updates
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
    private void initializeLocationUpdates() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.getAccuracy() <= MIN_DISTANCE_BETWEEN_UPDATES) {
                    GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    updateLocationOnMap(userLocation);

                    stopLocationUpdates();
                    isFirstLocationUpdate = false;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                showToast("GPS provider is disabled. Please enable it.");
            }
        };

        // Request location updates
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_BETWEEN_UPDATES, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                GeoPoint userLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                updateLocationOnMap(userLocation);
            }
        }
    }

    private void updateLocationOnMap(GeoPoint geoPoint) {
        mapController.setCenter(geoPoint);
    }


    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }
}