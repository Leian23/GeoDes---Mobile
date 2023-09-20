package com.example.geodes_mobile;
import android.content.Intent; // For creating an Intent to switch activities
import android.os.Bundle; // For handling the activity lifecycle and data
import android.view.View; // For handling user interface elements
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;




public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getStartedbtn = findViewById(R.id.getstartbtn);
        TextView startSigninText = findViewById(R.id.start_signin);

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


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



    }
}
