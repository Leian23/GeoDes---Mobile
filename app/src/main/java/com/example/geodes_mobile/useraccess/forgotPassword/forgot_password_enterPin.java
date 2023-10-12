package com.example.geodes_mobile.useraccess.forgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geodes_mobile.R;

public class forgot_password_enterPin extends AppCompatActivity {

    private Button confirmPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword_enterpin);

        confirmPin = findViewById(R.id.PinConfirm);

        confirmPin.setOnClickListener(view -> {
            Intent intent = new Intent(forgot_password_enterPin.this, forgot_password_changePass.class);
            startActivity(intent);
        });
    }
}
