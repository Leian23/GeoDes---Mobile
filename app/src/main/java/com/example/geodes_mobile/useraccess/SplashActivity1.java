package com.example.geodes_mobile.useraccess;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.map_home;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity1 extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String FIRST_TIME_KEY = "isFirstTime";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragments_onboard);

        // Check if it's the first time the user is opening the app
        if (isFirstTime()) {
            // Show onboarding layout
            // You can customize the logic here, e.g., show a dialog or navigate to the onboarding screen

            // After showing the onboarding, set isFirstTime to false
            setFirstTime(false);
        } else {
            // The user has opened the app before, you might want to navigate to the main activity or do nothing
            checkAndStartMainActivity();
        }

        // You can handle the button click for granting permissions here
        Button permissionButton = findViewById(R.id.permissionButton);
        permissionButton.setOnClickListener(v -> checkAndRequestPermissions());
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(FIRST_TIME_KEY, true);
        Log.d("OnboardingActivity", "isFirstTime: " + isFirstTime);
        return isFirstTime;
    }

    private void setFirstTime(boolean isFirstTime) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_TIME_KEY, isFirstTime);
        editor.apply();
    }

    private void checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_BOOT_COMPLETED);

        // Check notification permission
        boolean isNotificationPermissionGranted = NotificationManagerCompat.from(this).areNotificationsEnabled();

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            Log.d("OnboardingActivity", "Requesting permissions");
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {
            checkAndStartMainActivity();
        }
    }

    private void checkAndStartMainActivity() {
        Log.d("OnboardingActivity", "Starting MainActivity");
        Intent intent = new Intent(this, map_home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            // Check if all permissions are granted in grantResults
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Log.d("OnboardingActivity", "All permissions granted, starting MainActivity");
                startMainActivity();
            } else {
                // Handle case when not all permissions are granted
                Log.d("OnboardingActivity", "Not all permissions granted");
                // You might want to inform the user about the missing permissions or ask again
            }
        }
    }

    private void startMainActivity() {
        Log.d("OnboardingActivity", "Starting MainActivity");
        Intent intent = new Intent(this, map_home.class);
        startActivity(intent);
        finish();
    }
}
