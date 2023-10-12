// Corrected class name to match the file name
package com.example.geodes_mobile.useraccess.forgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geodes_mobile.R;

public class forgot_password extends AppCompatActivity {

    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        confirmBtn = findViewById(R.id.confirmEmail);

        confirmBtn.setOnClickListener(view -> {
            Intent intent = new Intent(forgot_password.this, forgot_password_enterPin.class);
            startActivity(intent);
        });


    }


}
