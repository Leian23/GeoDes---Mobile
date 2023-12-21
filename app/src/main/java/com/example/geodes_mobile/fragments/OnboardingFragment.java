package com.example.geodes_mobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String FIRST_TIME_KEY = "isFirstTime";

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_onboard, container, false);

        // Check if it's the first time the user is opening the app
        if (isFirstTime()) {
            // Show onboarding layout
            // You can customize the logic here, e.g., show a dialog or navigate to the onboarding screen

            // After showing the onboarding, set isFirstTime to false
            setFirstTime(false);
        } else {
            // The user has opened the app before, you might want to navigate to the main activity or do nothing
        }

        // You can handle the button click for granting permissions here
        Button permissionButton = view.findViewById(R.id.permissionButton);
        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle permission granting logic
                int camera = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
                int storage = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int loc = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE);
                int loc2 = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (storage != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (loc2 != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(android.Manifest.permission.CALL_PHONE);
                }
                if (loc != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(android.Manifest.permission.POST_NOTIFICATIONS);
                }

                if (!listPermissionsNeeded.isEmpty()) {
                    Log.d("OnboardingFragment", "Requesting permissions");
                    ActivityCompat.requestPermissions(requireActivity(), listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                } else {
                    Log.d("OnboardingFragment", "Starting MainActivity");
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(FIRST_TIME_KEY, true);
        Log.d("OnboardingFragment", "isFirstTime: " + isFirstTime);
        return isFirstTime;
    }

    private void setFirstTime(boolean isFirstTime) {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_TIME_KEY, isFirstTime);
        editor.apply();
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
                Log.d("OnboardingFragment", "All permissions granted, starting MainActivity");

            } else {
                // Handle case when not all permissions are granted
                Log.d("OnboardingFragment", "Not all permissions granted");
            }
        }
    }
}
