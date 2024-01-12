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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.fragments.MyPreferenceFragment;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


    public class GeofenceBroadcastReceiver extends BroadcastReceiver {

        private static final String TAG = "GeofenceReceiver";

        private static final String CHANNEL_ID = "GeofenceChannel";
        private static final int NOTIFICATION_ID_OUTER = 1;
        private static final int NOTIFICATION_ID_INNER = 2;
        private static final int ALARM_NOTIFICATION_ID = 3;
        public static final int DISMISS_NOTIFICATION_ID = 4;
        private FirebaseFirestore db;
        private static final String PREFS_NAME = "GeofencePrefs";
        //private SimpleDateFormat timeFormat, currentTime;
        private Date get_time, get_current;

        private String fenceName;
        private String alertType;
        private String user_email;

        @Override
        public void onReceive(Context context, Intent intent) {
            // Acquire a wake lock to ensure the device stays awake during processing
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager == null) {
                Log.e(TAG, "PowerManager is null");
                return;
            }

            // Update the WakeLock tag with a unique prefix
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.example.geodes_mobile:GeofenceWakeLock");
            wakeLock.acquire();

            try {
                // Your existing code for handling geofence events
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
            } finally {
                if (wakeLock != null && wakeLock.isHeld()) {
                    wakeLock.release();
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
        MyPreferenceFragment preferenceFragment = new MyPreferenceFragment();
        alertType = preferenceFragment.getAlertTypePreference(context);;

        if (value1 > 2000 && value1 <= 4000) {

            if ("outer Alert".equals(alertType)) {
                scheduleAlarm(context, geofenceName);
                showDismissNotificationInner(context);
            } else {
                Notification outerNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Outer Geofence Entry")
                        .setContentText("You are near your destination: " + geofenceName)
                        .setPriority(NotificationCompat.PRIORITY_HIGH) // Set higher priority
                        .setSmallIcon(R.drawable.marker_loc) // Replace with your actual notification icon for the outer geofence
                        .build();

                if (notificationManager != null) {
                    notificationManager.notify(NOTIFICATION_ID_OUTER, outerNotification);
                }

            }



            Log.d(TAG, "onReceive: Entered OuterGeofence. You are near your destination.");
            Toast.makeText(context, "You are near on " + geofenceName, Toast.LENGTH_SHORT).show();

        } else if (value1 <= 2000) {

            if ("inner Alert".equals(alertType)) {
                Log.d(TAG, "GeoFence: Inner");
                //Get schedule
                db.collection("geofenceSchedule")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                if (task1.isSuccessful()) {
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                    user_email = prefs.getString("user_email", "");
                                    Log.d(TAG, "Successful: " + user_email);
                                    for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                        String email = document1.getString("Email");
                                        if(email.equalsIgnoreCase(user_email)){

                                            List<String> selectedItemsIds = (List<String>) document1.get("selectedItemsIds");
                                            for (String itemId : selectedItemsIds) {

                                                //Get GeoEntry
                                                db.collection("geofencesEntry")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        String userid = document.getId();

                                                                        if(userid.equalsIgnoreCase(itemId)){

                                                                            //Get day
                                                                            boolean monday = document1.getBoolean("Monday");
                                                                            boolean tuesday = document1.getBoolean("Tuesday");
                                                                            boolean wednesday = document1.getBoolean("Wednesday");
                                                                            boolean thursday = document1.getBoolean("Thursday");
                                                                            boolean friday = document1.getBoolean("Friday");
                                                                            boolean saturday = document1.getBoolean("Saturday");
                                                                            boolean sunday = document1.getBoolean("Sunday");

                                                                            //Check current time and from database
                                                                            String time = document1.getString("Time");
                                                                            SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm a");
                                                                            String currentTime = sdf1.format(new Date());
                                                                            String pattern = "h:mm a";
                                                                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                                                                            Date time1 = null;
                                                                            Date time2 = null;
                                                                            try {
                                                                                time1 = sdf.parse(currentTime);
                                                                                time2 = sdf.parse(time);
                                                                            } catch (ParseException e) {
                                                                                throw new RuntimeException(e);
                                                                            }
                                                                            Calendar cal = Calendar.getInstance();
                                                                            cal.setTime(new Date());


                                                                            if(time1.after(time2)) {
                                                                                Log.d(TAG, "Time: After");
                                                                                if(monday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
                                                                                    checkIfScheduled(context,geofenceName);
                                                                                }
                                                                                if(tuesday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
                                                                                    checkIfScheduled(context,geofenceName);
                                                                                }
                                                                                if(wednesday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
                                                                                    checkIfScheduled(context,geofenceName);
                                                                                }
                                                                                if(thursday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
                                                                                    checkIfScheduled(context,geofenceName);
                                                                                }
                                                                                if(friday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
                                                                                    checkIfScheduled(context,geofenceName);
                                                                                }
                                                                                if(saturday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
                                                                                    checkIfScheduled(context,geofenceName);
                                                                                }
                                                                                if(sunday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
                                                                                    checkIfScheduled(context,geofenceName);
                                                                                }
                                                                                else {
                                                                                    //Nothing to do
                                                                                    Log.d(TAG, "Time: Before");
                                                                                }
                                                                            }
                                                                            else {
                                                                                //Nothing to do
                                                                                Log.d(TAG, "Time: Before");
                                                                            }

                                                                        }
                                                                        Log.d(TAG, "Alert: Inside Schedule.");
                                                                        return;
                                                                    }

                                                                } else {
                                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                                }
                                                            }
                                                        });

                                            }

                                            //Show if alert is not with schedule
                                            //scheduleAlarm(context, geofenceName);
                                            //showDismissNotificationOuter(context);
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task1.getException());
                                }
                            }
                        });
            } else {
                Notification innerNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Inner Geofence Entry")
                        .setContentText("You have Arrived on: " + geofenceName)
                        .setPriority(NotificationCompat.PRIORITY_HIGH) // Set higher priority
                        .setSmallIcon(R.drawable.marker_loc) // Replace with your actual notification icon for the outer geofence
                        .build();

                if (notificationManager != null) {
                    notificationManager.notify(NOTIFICATION_ID_OUTER, innerNotification);
                }

            }
            Log.d(TAG, "Alert: Not in Schedule.");
            scheduleAlarm(context, geofenceName);
            showDismissNotificationInner(context);
            Log.d(TAG, "onReceive: Entered InnerGeofence. Alarm scheduled.");
            Toast.makeText(context, "You have arrived on " + geofenceName, Toast.LENGTH_SHORT).show();

        }
    }


    private void handleExitEvent(Context context, String geofenceName) {
        //Get schedule
        db.collection("geofenceSchedule")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                        if (task1.isSuccessful()) {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            user_email = prefs.getString("user_email", "");
                            Log.d(TAG, "Successful: " + user_email);
                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                String email = document1.getString("Email");
                                if(email.equalsIgnoreCase(user_email)){

                                    List<String> selectedItemsIds = (List<String>) document1.get("selectedItemsIds");
                                    for (String itemId : selectedItemsIds) {

                                        //Get GeoEntry
                                        db.collection("geofencesEntry")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String userid = document.getId();

                                                                if(userid.equalsIgnoreCase(itemId)){

                                                                    //Get day
                                                                    boolean monday = document1.getBoolean("Monday");
                                                                    boolean tuesday = document1.getBoolean("Tuesday");
                                                                    boolean wednesday = document1.getBoolean("Wednesday");
                                                                    boolean thursday = document1.getBoolean("Thursday");
                                                                    boolean friday = document1.getBoolean("Friday");
                                                                    boolean saturday = document1.getBoolean("Saturday");
                                                                    boolean sunday = document1.getBoolean("Sunday");

                                                                    //Check current time and from database
                                                                    String time = document1.getString("Time");
                                                                    SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm a");
                                                                    String currentTime = sdf1.format(new Date());
                                                                    String pattern = "h:mm a";
                                                                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                                                                    Date time1 = null;
                                                                    Date time2 = null;
                                                                    try {
                                                                        time1 = sdf.parse(currentTime);
                                                                        time2 = sdf.parse(time);
                                                                    } catch (ParseException e) {
                                                                        throw new RuntimeException(e);
                                                                    }
                                                                    Calendar cal = Calendar.getInstance();
                                                                    cal.setTime(new Date());


                                                                    if(time1.after(time2)) {
                                                                        Log.d(TAG, "Time: After");
                                                                        if(monday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
                                                                            checkIfScheduled(context,geofenceName);
                                                                        }
                                                                        if(tuesday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
                                                                            checkIfScheduled(context,geofenceName);
                                                                        }
                                                                        if(wednesday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
                                                                            checkIfScheduled(context,geofenceName);
                                                                        }
                                                                        if(thursday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
                                                                            checkIfScheduled(context,geofenceName);
                                                                        }
                                                                        if(friday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
                                                                            checkIfScheduled(context,geofenceName);
                                                                        }
                                                                        if(saturday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
                                                                            checkIfScheduled(context,geofenceName);
                                                                        }
                                                                        if(sunday==true&&cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
                                                                            checkIfScheduled(context,geofenceName);
                                                                        }
                                                                        else {
                                                                            //Nothing to do
                                                                            Log.d(TAG, "Time: Before");
                                                                        }
                                                                    }
                                                                    else {
                                                                        //Nothing to do
                                                                        Log.d(TAG, "Time: Before");
                                                                    }

                                                                }
                                                                Log.d(TAG, "Alert: Inside Schedule.");
                                                                return;
                                                            }

                                                        } else {
                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });

                                    }

                                    //Show if alert is not with schedule
                                    //scheduleAlarm(context, geofenceName);
                                    //showDismissNotificationOuter(context);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task1.getException());
                        }
                    }
                });

        // Handle exit events if needed
        Log.d(TAG, "onReceive: EXIT from " + geofenceName);
        Toast.makeText(context, "Exit event from: " + geofenceName, Toast.LENGTH_SHORT).show();
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

    private void showDismissNotificationOuter(Context context) {
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
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 5, dismissIntent, flags);

        // Build the notification
        Notification dismissNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("You are near on:" + " " + fenceName)
                .setContentText("Click to dismiss the alert")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.marker_loc)
                .addAction(R.drawable.marker_loc, "Dismiss", dismissPendingIntent)
                .build();

        // Show the notification
        NotificationManagerCompat.from(context).notify(DISMISS_NOTIFICATION_ID, dismissNotification);
    }
        public void checkIfScheduled(Context context, String geofenceName){
            //Put here condition if user select with alarm inner
            Log.d(TAG, "Is inside scheduled");
            scheduleAlarm(context, geofenceName);
            showDismissNotificationOuter(context);
        }


    private void showDismissNotificationInner(Context context) {
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
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 6, dismissIntent, flags);

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


    private void showDismissNotificationExit(Context context) {
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
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 7, dismissIntent, flags);

        // Build the notification
        Notification dismissNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("You have exited:" + " " + fenceName)
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
            // If URI is null, use the default ringtone URI
            Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            return defaultRingtoneUri;
        }
    }

}