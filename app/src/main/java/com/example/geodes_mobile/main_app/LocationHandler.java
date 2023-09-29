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
    private static final long MIN_TIME_BETWEEN_UPDATES = 0; // 500 milliseconds
    private static final float MIN_DISTANCE_BETWEEN_UPDATES = 10;
    private static final int FASTEST_UPDATE_INTERVAL = 0; // milliseconds

    private Context context;
    private MapView mapView;
    private IMapController mapController;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isGpsProviderEnabled = true;
    private boolean isLocationUpdateRequested = false;

    public LocationHandler(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;

        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        mapController = mapView.getController();
        mapController.setZoom(17.0); // Adjust the zoom level based on your requirements

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

        // Check and request location permissions at runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        }

        initializeLocationUpdates();
    }

    public void requestLocationUpdate() {
        // Check if a location update is already requested
        if (!isLocationUpdateRequested) {
            initializeLocationUpdates();
            isLocationUpdateRequested = true;
        }
    }

    private void initializeLocationUpdates() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                updateLocationOnMap(userLocation);

                // Stop location updates after the first update
                stopLocationUpdates();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                if (!isGpsProviderEnabled) {
                    isGpsProviderEnabled = true;
                    requestLocationUpdates(); // Request new location updates when GPS is enabled again
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                showToast("GPS provider is disabled. Please enable it.");
                isGpsProviderEnabled = false;
            }
        };

        // Request location updates
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener); // Remove previous updates

            // Request new location updates from both GPS and NETWORK providers with faster intervals
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_BETWEEN_UPDATES, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_BETWEEN_UPDATES, locationListener);

            // Fetch the last known location if available using a faster interval

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

    // Properly release location updates when they are no longer needed
    public void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
            isLocationUpdateRequested = false; // Reset the flag when updates are stopped
        }
    }
}
