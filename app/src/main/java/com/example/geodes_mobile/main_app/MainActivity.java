package com.example.geodes_mobile.main_app;
import android.content.Intent; // For creating an Intent to switch activities
import android.os.Bundle; // For handling the activity lifecycle and data
import android.view.View; // For handling user interface elements
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.useraccess.signupActivity;
import com.example.geodes_mobile.useraccess.useraccessActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Button getStartedbtn = findViewById(R.id.getstartbtn);
        TextView startSigninText = findViewById(R.id.start_signin);
        TextView skiprocess = findViewById(R.id.skiprocess);

        startSigninText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to navigate to the useraccessActivity
                Intent intent = new Intent(MainActivity.this, useraccessActivity.class);
                startActivity(intent);
            }
        });

        getStartedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Not running, force closing the application
                Intent intent = new Intent(MainActivity.this, signupActivity.class);
                startActivity(intent);
            }
        });

        skiprocess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Not running, force closing the application
                Intent intent = new Intent(MainActivity.this, map_home.class);
                startActivity(intent);
            }
        });



        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



    }
}
