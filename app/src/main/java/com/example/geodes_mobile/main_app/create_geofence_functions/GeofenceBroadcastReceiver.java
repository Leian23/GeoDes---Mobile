package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.geodes_mobile.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceReceiver";
    private static final String CHANNEL_ID = "GeofenceChannel";
    private static final int NOTIFICATION_ID_OUTER = 1;
    private static final int NOTIFICATION_ID_INNER = 2;

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
                    // Entry Events
                    Log.d(TAG, "onReceive: ENTER from " + fenceName);
                    handleEntryEvent(context, fenceName, geofence);
                } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    // Exit Events
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

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Geofence Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (value1 > 2000 && value1 <= 4000) {
            // Outer Geofence Notification
            Notification outerNotification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("Outer Geofence Entry")
                    .setContentText("You are near your destination: " + geofenceName)
                    .setPriority(Notification.PRIORITY_HIGH) // Set higher priority
                    .setSmallIcon(R.drawable.marker_loc) // Replace with your actual notification icon for outer geofence
                    .build();

            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID_OUTER, outerNotification);
            }

            Log.d(TAG, "onReceive: Entered OuterGeofence. You are near your destination.");
            Toast.makeText(context, "You are near on." + geofenceName, Toast.LENGTH_SHORT).show();
            // Perform actions for being near the destination
        } else if (value1 <= 2000) {
            // Inner Geofence Notification
            Notification innerNotification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("Inner Geofence Entry")
                    .setContentText("You have arrived at your destination: " + geofenceName)
                    .setPriority(Notification.PRIORITY_HIGH) // Set higher priority
                    .setSmallIcon(R.drawable.marker_loc) // Replace with your actual notification icon for inner geofence
                    .build();

            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID_INNER, innerNotification);
            }

            Log.d(TAG, "onReceive: Entered InnerGeofence. You have arrived.");
            Toast.makeText(context, "You have arrived on " + geofenceName, Toast.LENGTH_SHORT).show();
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