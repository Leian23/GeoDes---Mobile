package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_RECEIVE_GEOFENCE = "com.example.geodes_mobile.RECEIVE_GEOFENCE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_RECEIVE_GEOFENCE)) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            if (geofencingEvent.hasError()) {
                // Handle geofence error
                return;
            }

            int transitionType = geofencingEvent.getGeofenceTransition();
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                // User entered a geofence
                showNotification(context, "Entered the geofence", "You entered the geofence.");
            } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                // User exited a geofence
                showNotification(context, "Exited the geofence", "You exited the geofence.");
            }
        }
    }

    private void showNotification(Context context, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0 and above
            NotificationChannel channel = new NotificationChannel("geofence_channel", "Geofence Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "geofence_channel")
                .setSmallIcon(org.osmdroid.wms.R.drawable.ic_menu_mylocation)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}