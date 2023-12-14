package com.example.geodes_mobile.useraccess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.geodes_mobile.Constants;
import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.map_home;
import com.example.geodes_mobile.useraccess.forgotPassword.forgot_password;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class useraccessActivity extends AppCompatActivity {
    private EditText userNameEditText, userPassEditText;
    private Button loginButton;
    private TextView forgotPassword;
    private FirebaseFirestore db;
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
        forgotPassword = findViewById(R.id.forgot_pass);

        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(useraccessActivity.this, forgot_password.class);
            startActivity(intent);
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
                            db.collection("users").whereEqualTo("email", email)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@android.support.annotation.NonNull Task<QuerySnapshot> task1) {
                                            if (task1.isSuccessful()) {

                                                for (QueryDocumentSnapshot document1 : task1.getResult()) {

                                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString("user_email",mAuth.getCurrentUser().getEmail());
                                                    editor.putString("contact_person",document1.getString("contact_person"));
                                                    editor.apply();
                                                    Log.d("UserFound", "UserFound: " + document1.getString("email"));

                                                }
                                                Toast.makeText(getApplication(), "Contact Person" + Constants.contact_person, Toast.LENGTH_SHORT).show();
                                            } else {

                                            }
                                        }
                                    });




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
