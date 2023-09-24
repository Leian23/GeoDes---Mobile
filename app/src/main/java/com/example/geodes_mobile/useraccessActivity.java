package com.example.geodes_mobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class useraccessActivity extends AppCompatActivity {
    private EditText userNameEditText, userPassEditText;
    private Button loginButton;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useraccess);
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        userNameEditText = findViewById(R.id.userName);
        userPassEditText = findViewById(R.id.userPass);
        loginButton = findViewById(R.id.siButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }
    private void loginUser() {
        String email = userNameEditText.getText().toString().trim();
        String password = userPassEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate the user using Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User login successful
                            Toast.makeText(useraccessActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(useraccessActivity.this, map_home.class);
                            startActivity(intent);
                        } else {
                            // User login failed
                            Toast.makeText(useraccessActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
