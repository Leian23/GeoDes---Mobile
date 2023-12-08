package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {

    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm received");

        // Retrieve the alarm ringtone URI from the intent
        Uri alarmRingtoneUri = intent.getParcelableExtra("ALARM_RINGTONE_URI");
        Log.d("AlarmReceiver", "Alarm ringtone URI: " + alarmRingtoneUri);

        // Check if the alarmRingtoneUri is null
        if (alarmRingtoneUri == null) {
            Log.e("AlarmReceiver", "Alarm ringtone URI is null");
            return;
        }

        // Retrieve the user's preference for vibration
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableVibration = sharedPreferences.getBoolean("enable_vibration", true);
        Log.d("AlarmReceiver", "Enable Vibration: " + enableVibration);

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, alarmRingtoneUri);
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());

                // Retrieve the user's preference for volume
                int volume = sharedPreferences.getInt("volume", 50); // Default volume if not set
                float volumeLevel = volume / 100.0f; // Convert to a float between 0.0 and 1.0
                mediaPlayer.setVolume(volumeLevel, volumeLevel);

                // Set up asynchronous preparation
                mediaPlayer.prepareAsync();

                // Set up a callback for when the preparation is completed
                mediaPlayer.setOnPreparedListener(mp -> {
                    Log.d("AlarmReceiver", "MediaPlayer setup completed, starting playback");
                    mp.start();

                    // Vibrate if enabled
                    if (enableVibration) {
                        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        if (vibrator != null) {
                            long[] pattern = {0, 1000, 1000}; // Vibrate for 1 second, wait for 1 second, repeat
                            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                        }
                    }
                });

            } catch (IOException e) {
                Log.e("AlarmReceiver", "Error setting up MediaPlayer: " + e.getMessage());
                releaseMediaPlayer();
            }
        }
    }

    static void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            releaseMediaPlayer();
        }

        // Stop vibration if enabled
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private static void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
