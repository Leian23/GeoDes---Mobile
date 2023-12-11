package com.example.geodes_mobile.useraccess.forgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.geodes_mobile.R;
import com.example.geodes_mobile.useraccess.useraccessActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgot_password extends AppCompatActivity {

    private EditText emailEditText;
    private Button confirmBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.forgotPassEmail);
        confirmBtn = findViewById(R.id.confirmEmail);

        confirmBtn.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();

            if (!email.isEmpty()) {
                resetPassword(email);
            } else {
                showToast("Please enter your email address");
            }
        });
    }

    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            // You may show a success message or navigate to the next screen
                            showToast("Password reset email sent successfully");
                            Intent intent = new Intent(forgot_password.this, useraccessActivity.class);
                            startActivity(intent);
                        } else {
                            // Handle errors, e.g., if the email address is not registered
                            // You may show a Toast or setError on the EditText
                            showToast("Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(forgot_password.this, message, Toast.LENGTH_SHORT).show();
    }
}
