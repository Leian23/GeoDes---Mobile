// AlarmReceiver.java
package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm received");

        // Retrieve the alarm ringtone URI from the intent
        Uri alarmRingtoneUri = intent.getParcelableExtra("ALARM_RINGTONE_URI");
        Log.d("AlarmReceiver", "Alarm ringtone URI: " + alarmRingtoneUri);

        if (mediaPlayer == null) {
            // Play the alarm with the specified ringtone
            new MediaPlayerSetupTask().execute(context, alarmRingtoneUri);
        }
    }

    static void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            releaseMediaPlayer();
        }
    }

    private static void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private static class MediaPlayerSetupTask extends AsyncTask<Object, Void, MediaPlayer> {

        @Override
        protected MediaPlayer doInBackground(Object... params) {
            Context context = (Context) params[0];
            Uri alarmRingtoneUri = (Uri) params[1];

            MediaPlayer mediaPlayer = MediaPlayer.create(context, alarmRingtoneUri);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
            }

            return mediaPlayer;
        }

        @Override
        protected void onPostExecute(MediaPlayer result) {
            if (result != null) {
                mediaPlayer = result;
                Log.d("AlarmReceiver", "MediaPlayer created asynchronously, starting playback");
                mediaPlayer.start();
            } else {
                Log.e("AlarmReceiver", "Error creating MediaPlayer asynchronously");
            }
        }
    }
}
