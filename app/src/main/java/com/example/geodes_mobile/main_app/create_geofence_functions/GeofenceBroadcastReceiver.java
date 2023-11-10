package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Geofencing error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();
            for (Geofence geofence : triggeredGeofences) {
                if (geofence.getRequestId().equals("outer")) {
                    // User entered the outer geofence
                    Log.d(TAG, "User entered the outer geofence");
                } else if (geofence.getRequestId().equals("inner")) {
                    // User entered the inner geofence
                    Log.d(TAG, "User entered the inner geofence");
                    // Trigger an alert or notification here
                }
            }
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();
            for (Geofence geofence : triggeredGeofences) {
                if (geofence.getRequestId().equals("outer")) {
                    // User exited the outer geofence
                    Log.d(TAG, "User exited the outer geofence");
                } else if (geofence.getRequestId().equals("inner")) {
                    // User exited the inner geofence
                    Log.d(TAG, "User exited the inner geofence");
                }
            }
        }
    }
}
