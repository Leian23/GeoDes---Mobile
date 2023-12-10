package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DismissReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("com.example.geodes_mobile.ACTION_DISMISS_ALARM")) {
            // Add code here to handle the dismissal of the alarm
            Log.d("DismissReceiver", "Alarm dismissed");

            // Stop the alarm when dismissed
            AlarmReceiver.stopAlarm();

            // Cancel the dismiss notification
            cancelDismissNotification(context);

            // You might want to send a broadcast or update UI to reflect that the alarm has been dismissed
            Intent dismissIntent = new Intent("com.example.geodes_mobile.ALARM_DISMISSED");
            LocalBroadcastManager.getInstance(context).sendBroadcast(dismissIntent);
        }
    }

    private void cancelDismissNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(GeofenceBroadcastReceiver.DISMISS_NOTIFICATION_ID);
    }
}