package com.example.geodes_mobile.main_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.geodes_mobile.Constants;
import com.example.geodes_mobile.ForegroundService;
import com.example.geodes_mobile.R;
import com.example.geodes_mobile.useraccess.SplashActivity;
import com.example.geodes_mobile.useraccess.SplashActivity1;
import com.example.geodes_mobile.useraccess.signupActivity;
import com.example.geodes_mobile.useraccess.useraccessActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    public static final String CHANNEL_ID = "Emergency SOS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Constants.user_email = prefs.getString("user_email", "");
        Constants.contact_person = prefs.getString("contact_person", "");
        Button getStartedbtn = findViewById(R.id.getstartbtn);
        TextView startSigninText = findViewById(R.id.start_signin);
        TextView skipButton = findViewById(R.id.skiprocess);



        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, map_home.class);
            startActivity(intent);
        }

        startSigninText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, useraccessActivity.class);
            startActivity(intent);
        });

        getStartedbtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, signupActivity.class);
            startActivity(intent);
        });

        skipButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SplashActivity1.class);
            startActivity(intent);
        });

        if (Constants.user_email == null || Constants.user_email.isEmpty()) {
            String unique_id = getRandomString(20);
            if (unique_id != null && !unique_id.isEmpty()) { // Check if unique_id is not empty
                Log.d(MainActivity.class.getSimpleName(), "Generated unique_id: " + unique_id);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user_email", unique_id);
                editor.apply();

                String firstName = "Anonymous";
                String lastName = "";
                String password = "";

                Map<String, Object> user = new HashMap<>();
                user.put("first", firstName);
                user.put("last", lastName);
                user.put("email", unique_id);

                fStore.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                                // After adding user data to Firestore, create a user account with Firebase Authentication
                                if (mAuth.getCurrentUser() == null) {
                                    mAuth.createUserWithEmailAndPassword(unique_id, password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // User account created successfully
                                                        Toast.makeText(MainActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                                        // Additional actions if needed (e.g., navigate to another activity)
                                                    } else {
                                                        // If account creation fails, log the error
                                                        Log.e(TAG, "Error creating user account", task.getException());
                                                        Toast.makeText(MainActivity.this, "Error creating user account", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(MainActivity.this, "Error adding user data", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        startForegroundService(serviceIntent);
        foregroundServiceRunning();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
