package com.example.geodes_mobile.main_app.homebtn_functions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geodes_mobile.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AlertEditDialog extends Dialog {

    private Context context;
    private Button discardButton;
    private Button saveUpdate;
    private EditText alertNameEditText;
    private EditText outerRadius;
    private EditText innerRadius;
    private EditText alertNotes;
    private Boolean updateGeo;
    private String alertDocumentId; // Firestore document ID
    private DocumentReference alertDocRef;



    public AlertEditDialog(Context context, String documentId) {
        super(context);
        this.context = context;
        this.alertDocumentId = documentId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_sheet_alert_edit);

        discardButton = findViewById(R.id.discardEditAlert);
        saveUpdate = findViewById(R.id.saveEditAlert);
        alertNameEditText = findViewById(R.id.NameAlertBox12);
        outerRadius = findViewById(R.id.OuterGeofenceTextBox);
        innerRadius = findViewById(R.id.InnerGeofenceTextBox);
        alertNotes = findViewById(R.id.EditGeofencingNotes);

        // Load data from Firestore if document ID is provided
        if (alertDocumentId != null && !alertDocumentId.isEmpty()) {
            loadAlertDataFromFirestore();
        }

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        saveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFirestore();
                dismiss();
            }
        });



        getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner);
    }

    private void loadAlertDataFromFirestore() {
        alertDocRef = FirebaseFirestore.getInstance().collection("geofencesEntry").document(alertDocumentId);
        alertDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                alertNameEditText.setText(task.getResult().getString("alertName"));
                outerRadius.setText(String.valueOf(task.getResult().getDouble("outerRadius")));
                innerRadius.setText(String.valueOf(task.getResult().getDouble("innerRadius")));
                alertNotes.setText(task.getResult().getString("notes"));
            } else {
                Log.e("AlertEditDialog", "Error loading data from Firestore: " + task.getException());
            }
        });
    }

    private void updateFirestore() {
        String updatedAlertName = alertNameEditText.getText().toString();
        double updatedOuterRadius = Double.parseDouble(outerRadius.getText().toString());
        double updatedInnerRadius = Double.parseDouble(innerRadius.getText().toString());
        String updatedNotes = alertNotes.getText().toString();

        // Update the Firestore document with the edited details
        alertDocRef
                .update(
                        "alertName", updatedAlertName,
                        "outerRadius", updatedOuterRadius,
                        "innerRadius", updatedInnerRadius,
                        "notes", updatedNotes
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show();

                    // Introduce a short delay (e.g., 500 milliseconds) before refreshing isAlertEnabled
                    new Handler().postDelayed(() -> refreshIsAlertEnabled(), 500);
                })
                .addOnFailureListener(e -> {
                    Log.e("AlertEditDialog", "Error updating data in Firestore: " + e);
                });
    }

    private void refreshIsAlertEnabled() {
        // Fetch the current value of isAlertEnabled from Firestore
        alertDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean currentIsAlertEnabled = task.getResult().getBoolean("alertEnabled");

                // Toggle the value of isAlertEnabled
                boolean updatedIsAlertEnabled = !currentIsAlertEnabled;

                // Update the Firestore document with the toggled value of isAlertEnabled
                alertDocRef.update("alertEnabled", updatedIsAlertEnabled)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Data refreshed", Toast.LENGTH_SHORT).show();

                            // Toggle back to the original value after a short delay
                            new Handler().postDelayed(() -> {
                                alertDocRef.update("alertEnabled", currentIsAlertEnabled)
                                        .addOnSuccessListener(aVoid1 -> {
                                            Toast.makeText(context, "Data reverted", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AlertEditDialog", "Error updating data in Firestore: " + e);
                                        });
                            }, 500);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("AlertEditDialog", "Error updating data in Firestore: " + e);
                        });
            } else {
                Log.e("AlertEditDialog", "Error fetching current data from Firestore: " + task.getException());
            }
        });
    }

}