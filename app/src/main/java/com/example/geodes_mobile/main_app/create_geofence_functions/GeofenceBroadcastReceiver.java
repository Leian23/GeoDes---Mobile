package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "This is geofence!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive: Geofence broadcast received.");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent == null) {
            Log.e(TAG, "onReceive: GeofencingEvent is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "onReceive: Error receiving geofence event: " + geofencingEvent.getErrorCode());
            return;
        }

        List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();
        int transition = geofencingEvent.getGeofenceTransition();

        for (Geofence geofence : triggeredGeofences) {
            Log.d(TAG, "onReceive: Triggered Geofence - ID: " + geofence.getRequestId());
            String fenceName = intent.getStringExtra("GEOFENCE_NAME");
            if (fenceName != null) {
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    // Handle entry events
                    Log.d(TAG, "onReceive: ENTER from " + fenceName);
                    handleEntryEvent(context, fenceName, geofence);
                } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    // Handle exit events
                    Log.d(TAG, "onReceive: EXIT from " + fenceName);
                    handleExitEvent(context, fenceName);
                } else {
                    Log.d(TAG, "onReceive: Unexpected transition type: " + transition);
                }
            }
        }
    }

    private void handleEntryEvent(Context context, String geofenceName, Geofence geofence) {

         int value1 = Integer.parseInt(geofence.getRequestId());

        if  (value1 > 2000 && value1 <= 4000) {
            Log.d(TAG, "onReceive: Entered OuterGeofence. You are near your destination.");
            Toast.makeText(context, "You are near on." + geofenceName , Toast.LENGTH_SHORT).show();
            // Perform actions for being near the destination
        } else if (value1 <= 2000) {
            Log.d(TAG, "onReceive: Entered InnerGeofence. You have arrived.");
            Toast.makeText(context, "You have arrived. on " + geofenceName, Toast.LENGTH_SHORT).show();
            // Perform actions for arriving at the destination
        }
    }

    private void handleExitEvent(Context context, String geofenceName) {
        // Handle exit events if needed
        Log.d(TAG, "onReceive: EXIT from " + geofenceName);
        Toast.makeText(context, "Exit event from: " + geofenceName, Toast.LENGTH_SHORT).show();
        // Additional logic for exit, if needed
    }

}