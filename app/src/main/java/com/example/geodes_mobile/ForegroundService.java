package com.example.geodes_mobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.geodes_mobile.main_app.ConfirmationActivity;
import com.example.geodes_mobile.main_app.map_home;

public class ForegroundService extends Service{
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Log.e("Service", "Service is running...");
                            try {
                                Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.example.geodes_mobile");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Log.e("Service", "Opening App");
                                Thread.sleep(20000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();

        Intent dialogIntent = new Intent(this, map_home.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);



        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID).setOngoing(true)
                .setContentText("GeoDes is running....")
                .setContentTitle("GeoDes")
                .setSmallIcon(R.drawable.alarm_ic);

        startForeground(1001, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    public void openConfirmation(){
        Intent clickIntentEmergency = new Intent(this,ConfirmationActivity.class);
        clickIntentEmergency.putExtra("clicked","emergency");
        clickIntentEmergency.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startService(clickIntentEmergency);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
