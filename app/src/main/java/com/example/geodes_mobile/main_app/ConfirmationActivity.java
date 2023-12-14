package com.example.geodes_mobile.main_app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geodes_mobile.Constants;
import com.example.geodes_mobile.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class ConfirmationActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private TextView txt_message;
    private Button btn_cancel;

    private ProgressBar circularProgressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_show);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Log.e("Service", "Confirmation is running...");
                            try {
                                Thread.sleep(20000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();
        txt_message = findViewById(R.id.txt_message);
        btn_cancel = findViewById(R.id.btn_cancel);
        circularProgressBar = findViewById(R.id.progress_count);

        Intent intent = getIntent();
        String clicked = intent.getStringExtra("clicked");

        // Set the duration of the countdown in milliseconds (5 seconds)
        long countdownDuration = 5000;

        // Create a countdown timer that updates the progress bar
        new CountDownTimer(countdownDuration, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Calculate the progress based on the remaining time
                progressStatus = (int) (((countdownDuration - millisUntilFinished) / (float) countdownDuration) * 100);

                // Update the progress bar on the UI thread
                handler.post(new Runnable() {
                    public void run() {
                        circularProgressBar.setProgress(progressStatus);
                    }
                });
            }

            @Override
            public void onFinish() {
                // Ensure the progress bar reaches 100% when the countdown is finished
                progressStatus = 100;
                handler.post(new Runnable() {
                    public void run() {
                        circularProgressBar.setProgress(progressStatus);
                        if(clicked.equalsIgnoreCase("emergency")){
                            Intent clickIntentEmergency = new Intent(Intent.ACTION_CALL);
                            clickIntentEmergency.setData(Uri.parse(Uri.parse("tel:")+ Constants.contact_person));
                            clickIntentEmergency.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(clickIntentEmergency);
                            finish();
                        }
                        else {
                            playSound();
                        }
                    }
                });
            }
        }.start();



        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void playSound(){
        final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        MediaPlayer mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource("content://media/internal/audio/media/97");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            mp.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
            }
        });
    }

}
