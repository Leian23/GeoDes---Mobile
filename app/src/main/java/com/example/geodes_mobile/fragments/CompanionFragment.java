package com.example.geodes_mobile.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.geodes_mobile.Constants;
import com.example.geodes_mobile.R;
import com.example.geodes_mobile.ScanQRCodeActivity;
import com.example.geodes_mobile.main_app.MainActivity;
import com.example.geodes_mobile.main_app.map_home;
import com.example.geodes_mobile.useraccess.signupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CompanionFragment extends Fragment {

    private FirebaseFirestore db;
    private EditText txt_code;
    private Button btn_connect;
    private TextView btn_connect_qrcode;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_companion, container, false);

        db = FirebaseFirestore.getInstance();

        txt_code = rootView.findViewById(R.id.txt_code);
        btn_connect = rootView.findViewById(R.id.btn_connect);
        btn_connect_qrcode = rootView.findViewById(R.id.btn_connect_qrcode);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToSmartWatch();
            }
        });

        btn_connect_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ScanQRCodeActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void connectToSmartWatch(){

        db.collection("wareOS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if(document.getString("code").equalsIgnoreCase(txt_code.getText().toString())){
                                    Log.d(TAG, "WareOS ID: " + document.getId());
                                    //Get UserID
                                    db.collection("users")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                    if (task1.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                            String email = document1.getString("email");
                                                            if(email.equalsIgnoreCase(Constants.user_email)){

                                                                db.collection("users")
                                                                        .document(document1.getId()).update("selectedDeviceID", FieldValue.arrayUnion(document.getId()));
                                                                Toast.makeText(getContext(), "Companion Successfully Connected", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task1.getException());
                                                    }
                                                }
                                            });

                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}