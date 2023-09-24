package com.example.geodes_mobile;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity {
    private EditText firstNameTxt, lastNameTxt, emailTxt, passwordTxt, conpasswordTxt;
    private Button siButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        firstNameTxt = findViewById(R.id.firstnameTxt);
        lastNameTxt = findViewById(R.id.lastnameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        passwordTxt = findViewById(R.id.passwordTxt);
        conpasswordTxt = findViewById(R.id.conpasswordTxt);
        siButton = findViewById(R.id.siButton);

        siButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for network connectivity before attempting to register
                if (isNetworkAvailable()) {
                    registerUser();
                } else {
                    Toast.makeText(signupActivity.this, "Not Connected to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser() {
        final String firstName = firstNameTxt.getText().toString().trim();
        final String lastName = lastNameTxt.getText().toString().trim();
        final String email = emailTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();
        String confirmPassword = conpasswordTxt.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Store additional user information in Firebase Realtime Database
                            User userData = new User(firstName, lastName, email);
                            databaseReference.child(user.getUid()).setValue(userData);

                            Toast.makeText(signupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            // You can redirect to another activity here or perform any other action.
                        } else {
                            Toast.makeText(signupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = null;

        // Check if network is available and if it has internet connectivity
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) {
            networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        }

        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }



}
