package com.example.geodes_mobile.main_app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.useraccess.signupActivity;
import com.example.geodes_mobile.useraccess.useraccessActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Check if the user is already authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, finish MainActivity and go to the next screen
            Intent intent = new Intent(MainActivity.this, map_home.class);
            startActivity(intent);
            finish();
        }

        Button getStartedbtn = findViewById(R.id.getstartbtn);
        TextView startSigninText = findViewById(R.id.start_signin);
        TextView skiprocess = findViewById(R.id.skiprocess);

        startSigninText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, useraccessActivity.class);
            startActivity(intent);
        });

        getStartedbtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, signupActivity.class);
            startActivity(intent);
        });

        skiprocess.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, map_home.class);
            startActivity(intent);
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
