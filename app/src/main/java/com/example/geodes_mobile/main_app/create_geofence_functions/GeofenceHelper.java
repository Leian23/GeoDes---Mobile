package com.example.geodes_mobile.main_app.create_geofence_functions;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class GeofenceHelper {
    private static final String TAG = "GeofenceHelper";
    private static final float GEOFENCE_RADIUS_IN_METERS = 100; // Adjust as needed

    private Context context;
    private GeofencingClient geofencingClient;

    public GeofenceHelper(Context context) {
        this.context = context;
        this.geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public void addGeofences(GeoPoint center, double innerRadius) {
        List<Geofence> geofenceList = new ArrayList<>();

        // Create an outer geofence (not triggering any action)
        geofenceList.add(new Geofence.Builder()
                .setRequestId("outer")
                .setCircularRegion(center.getLatitude(), center.getLongitude(), GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        // Create an inner geofence that triggers an alert
        if (innerRadius > 0) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId("inner")
                    .setCircularRegion(center.getLatitude(), center.getLongitude(), (float) innerRadius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofences(geofenceList)
                .build();

        PendingIntent pendingIntent = getGeofencePendingIntent();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Geofences added"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add geofences: " + e.getMessage()));
    }

    public void removeGeofences() {
        List<String> geofenceIds = new ArrayList<>();
        geofenceIds.add("outer");
        geofenceIds.add("inner");
        geofencingClient.removeGeofences(geofenceIds)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Geofences removed"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to remove geofences: " + e.getMessage()));
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

