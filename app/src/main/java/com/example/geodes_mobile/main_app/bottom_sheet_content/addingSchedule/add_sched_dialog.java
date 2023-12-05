package com.example.geodes_mobile.main_app.bottom_sheet_content.addingSchedule;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.example.geodes_mobile.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class add_sched_dialog extends Dialog {

    public add_sched_dialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alert_layout);

        // Set custom height for the dialog window
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1000);
            window.setLayout(800, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        fetchAlertsFromFirestore();
    }

    private void fetchAlertsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference geofenceEntryCollection = db.collection("geofencesEntry");
        CollectionReference geofenceExitCollection = db.collection("geofencesExit");

        // Combine the results of both collections
        geofenceEntryCollection.get().addOnSuccessListener(entrySnapshots -> {
            List<AlertItem> alertItems = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : entrySnapshots) {
                String alertName = documentSnapshot.getString("alertName");
                String uniqueId = documentSnapshot.getId();

                AlertItem alertItem = new AlertItem(alertName, uniqueId, R.drawable.get_in);
                alertItems.add(alertItem);
            }

            // Fetch data from the "geofenceExit" collection
            geofenceExitCollection.get().addOnSuccessListener(exitSnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : exitSnapshots) {
                    String alertName = documentSnapshot.getString("alertName");
                    String uniqueId = documentSnapshot.getId();

                    AlertItem alertItem = new AlertItem(alertName, uniqueId, R.drawable.get_out);
                    alertItems.add(alertItem);
                }

                // Set up RecyclerView with the combined data
               // setUpRecyclerView(alertItems);
            }).addOnFailureListener(e -> {
                // Handle errors
            });

        }).addOnFailureListener(e -> {
            // Handle errors
        });
    }


   /* private void setUpRecyclerView(List<AlertItem> alertItems) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewAlerts);
        AlertAdapter5 adapter = new AlertAdapter5(alertItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    */
}



