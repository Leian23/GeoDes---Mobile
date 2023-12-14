package com.example.geodes_mobile.main_app;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.geodes_mobile.ConnectionNotification;

public class NotificationReceiver extends BroadcastReceiver {

    private ConnectionNotification listener;

    public NotificationReceiver(ConnectionNotification listener ){

        this.listener = listener;

    }
    @Override
    public void onReceive(Context context, Intent intent) {

        listener.playSos();

        /*Intent intent1 = new Intent();
        intent1.setClassName(context.getPackageName(), map_home.class.getName());
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);*/

        /*if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.sos);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());

        }
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();

        }
        else {
            mediaPlayer.start();
        }

        // Set volume to maximum
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        */

        /*Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation_show);


        progress_count = dialog.findViewById(R.id.progress_count);


        TextView txt_message = (TextView) dialog.findViewById(R.id.txt_message);

        txt_message.setText("Calling your contact person in");

        Button btn_cancel = (Button)dialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();*/
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //notificationManager.cancel(1);

    }
    // Handler to update the UI thread from the background thread

    public void playSound(){

    }
}