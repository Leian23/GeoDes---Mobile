package com.example.geodes_mobile.useraccess;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geodes_mobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText firstNameTxt, lastNameTxt, emailTxt, passwordTxt, conpasswordTxt;
    private Button siButton;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        firstNameTxt = findViewById(R.id.firstnameTxt);
        lastNameTxt = findViewById(R.id.lastnameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        passwordTxt = findViewById(R.id.passwordTxt);
        conpasswordTxt = findViewById(R.id.conpasswordTxt);
        siButton = findViewById(R.id.siButton);


          siButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String firstName = firstNameTxt.getText().toString().trim();
                    String lastName = lastNameTxt.getText().toString().trim();
                    String email = emailTxt.getText().toString().trim();
                    String password = passwordTxt.getText().toString().trim();
                    String confirmPassword = conpasswordTxt.getText().toString().trim();

                    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(signupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(signupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isNetworkAvailable()) {
                        Toast.makeText(signupActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> user = new HashMap<>();
                    user.put("first", firstName);
                    user.put("last", lastName);
                    user.put("email", email);

                    fStore.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                                    // After adding user data to Firestore, create a user account with Firebase Authentication
                                    mAuth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // User account created successfully
                                                        Toast.makeText(signupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                                        // Additional actions if needed (e.g., navigate to another activity)
                                                    } else {
                                                        // If account creation fails, log the error
                                                        Log.e(TAG, "Error creating user account", task.getException());
                                                        Toast.makeText(signupActivity.this, "Error creating user account", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    Toast.makeText(signupActivity.this, "Error adding user data", Toast.LENGTH_SHORT).show();
                                }
                            });

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
