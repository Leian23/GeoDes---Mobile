package com.example.geodes_mobile.main_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.geodes_mobile.ConnectionNotification;
import com.example.geodes_mobile.R;

public class NotificationReceiver extends BroadcastReceiver implements ConnectionNotification {

    private static MediaPlayer mediaPlayer;
    private static boolean isAlarmPlaying = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("ACTION_SOS_TOGGLE".equals(action)) {
            String toggleAction = intent.getStringExtra("action");
            if ("toggle".equals(toggleAction)) {
                if (isAlarmPlaying()) {
                    stopSos();
                } else {
                    playSos(context);
                }
            }
        }
    }


    @Override
    public void playSos(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.sos);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());

            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            // Set the volume to 50% of the maximum volume
            int targetVolume = (int) (0.5 * maxVolume);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSos();
                }
            });
        }

        if (!isAlarmPlaying) {
            mediaPlayer.start();
            isAlarmPlaying = true;
        }
    }

    private void stopSos() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            isAlarmPlaying = false;
        }
    }

    @Override
    public void playSos() {
        // You can implement this method if needed
    }

    private boolean isAlarmPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
