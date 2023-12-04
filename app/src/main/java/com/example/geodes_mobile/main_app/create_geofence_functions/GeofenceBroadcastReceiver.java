// GeofenceBroadcastReceiver.java
package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.geodes_mobile.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceReceiver";
    private static final String CHANNEL_ID = "GeofenceChannel";
    private static final int NOTIFICATION_ID_OUTER = 1;
    private static final int NOTIFICATION_ID_INNER = 2;
    private static final int ALARM_NOTIFICATION_ID = 3;
    public static final int DISMISS_NOTIFICATION_ID = 4;

    private String fenceName;

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
            fenceName = intent.getStringExtra("GEOFENCE_NAME");
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
            Notification outerNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Outer Geofence Entry")
                    .setContentText("You are near your destination: " + geofenceName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // Set higher priority
                    .setSmallIcon(R.drawable.marker_loc) // Replace with your actual notification icon for the outer geofence
                    .build();

            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID_OUTER, outerNotification);
            }

            Log.d(TAG, "onReceive: Entered OuterGeofence. You are near your destination.");
            Toast.makeText(context, "You are near on " + geofenceName, Toast.LENGTH_SHORT).show();

        } else if (value1 <= 2000) {

            scheduleAlarm(context, geofenceName);
            Log.d(TAG, "onReceive: Entered InnerGeofence. Alarm scheduled.");
            Toast.makeText(context, "You have arrived on " + geofenceName, Toast.LENGTH_SHORT).show();
            showDismissNotification(context);
        }
    }






    private void handleExitEvent(Context context, String geofenceName) {
        // Handle exit events if needed
        Log.d(TAG, "onReceive: EXIT from " + geofenceName);
        Toast.makeText(context, "Exit event from: " + geofenceName, Toast.LENGTH_SHORT).show();
        // Additional logic for exit, if needed
    }

    private void scheduleAlarm(Context context, String geofenceName) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("GEOFENCE_NAME", geofenceName);

        // Retrieve the selected alarm ringtone URI from preferences
        Uri selectedAlarmRingtoneUri = getSelectedAlarmRingtoneUri(context);
        intent.putExtra("ALARM_RINGTONE_URI", selectedAlarmRingtoneUri);

        // Use PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        int flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_NOTIFICATION_ID, intent, flags);

        long futureInMillis = System.currentTimeMillis(); // Trigger immediately

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
        }
    }

    private void showDismissNotification(Context context) {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Dismiss Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create an intent for the dismiss action
        Intent dismissIntent = new Intent(context, DismissReceiver.class);
        dismissIntent.setAction("com.example.geodes_mobile.ACTION_DISMISS_ALARM");

        // Use PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        int flags = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, flags);

        // Build the notification
        Notification dismissNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("You have Arrive on" + " " + fenceName)
                .setContentText("Click to dismiss the alert")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.marker_loc)
                .addAction(R.drawable.marker_loc, "Dismiss", dismissPendingIntent)
                .build();

        // Show the notification
        NotificationManagerCompat.from(context).notify(DISMISS_NOTIFICATION_ID, dismissNotification);
    }

    private Uri getSelectedAlarmRingtoneUri(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String uriString = sharedPreferences.getString("selected_alarm_ringtone_uri", null);

        if (uriString != null) {
            return Uri.parse(uriString);
        } else {
            // Return a default URI or null, depending on your requirements
            return null;
        }
    }
}
