package com.example.geodes_mobile.main_app.schedule_settings_adaptor;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Adapter4 extends RecyclerView.Adapter<Adapter4.ViewHolder> {
    private List<DataModel4> dataList;
    private Context context;
    private OnItemClickListener listener;
    private String alertName;

    public Adapter4(List<DataModel4> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sched_item_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel4 data = dataList.get(position);

        holder.titleTextView.setText(data.getSchedTitle());
        holder.timeSchedStart.setText(data.getTimeStart());
        holder.noteTextView.setText(data.getNote());
        holder.iconCalImageView.setImageResource(data.getIconCal());
        holder.entryImageImageView.setImageResource(data.getEntryImage());
        holder.iconMarkerImageView.setImageResource(data.getIconMarker());
        holder.scheduled.setText(data.getSchedules());
        holder.listOfAlerts.setText(data.getAlarmList());

        // Set an OnCheckedChangeListener for the switch
        holder.alertSwitch.setOnCheckedChangeListener(null); // Remove previous listener to avoid conflicts

        // Set the switch state based on the data
        holder.alertSwitch.setChecked(data.isAlertSwitchOn());

        // Set a new OnCheckedChangeListener for the switch
        holder.alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setAlertSwitchOn(isChecked);
                if (isChecked) {
                    Log.e(TAG, data.getSelectedItemsIds().toString());
                    Toast.makeText(context, "" + data.getSelectedItemsIds(), Toast.LENGTH_SHORT).show();

                   // compareIdsAndRetrieveAlertName(data.getSelectedItemsIds());
                } else {
                    Log.e(TAG, "ie false");
                }

                // Update Firestore with the new switch state
                updateFirestoreWithSwitchState(data);
            }
        });

        // Set an OnClickListener for the entire item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click here
                Toast.makeText(context, "Clicked: " + data.getSchedTitle(), Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onItemClick(data);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView timeSchedStart;
        TextView noteTextView;
        ImageView iconCalImageView;
        ImageView entryImageImageView;
        ImageView iconMarkerImageView;
        Switch alertSwitch;

        TextView scheduled;
        TextView listOfAlerts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.TitleSetSched);
            timeSchedStart = itemView.findViewById(R.id.TimeSched);
            noteTextView = itemView.findViewById(R.id.AlertDesc);
            iconCalImageView = itemView.findViewById(R.id.calendarImage);
            entryImageImageView = itemView.findViewById(R.id.AlarmIc);
            iconMarkerImageView = itemView.findViewById(R.id.clockSchedIc);
            alertSwitch = itemView.findViewById(R.id.AlertSwitch);
            scheduled = itemView.findViewById(R.id.ScheduleSched);
            listOfAlerts = itemView.findViewById(R.id.AlertDesc);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DataModel4 data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private void updateFirestoreWithSwitchState(DataModel4 data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("geofenceSchedule")
                .document(data.getUniqueId()) // Assuming you have a unique ID for each document
                .update("SchedStat", data.isAlertSwitchOn())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Switch state updated in Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error updating switch state in Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    private void compareIdsAndRetrieveAlertName(List<String> selectedIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String id : selectedIds) {
            // Compare with geofencesEntry
            db.collection("geofencesEntry")
                    .whereEqualTo("uniqueID", id)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                             alertName = documentSnapshot.getString("alertName");
                            if (alertName != null) {
                                Toast.makeText(context, "Match found in geofencesEntry. AlertName: " + alertName, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Match found in geofencesEntry. AlertName: " + alertName);
                                return; // Match found, no need to check further
                            }
                        }

                        // If no match is found in geofencesEntry, check geofencesExit
                        compareIdsAndRetrieveAlertNameFromExit(id);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error querying geofencesEntry", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void compareIdsAndRetrieveAlertNameFromExit(String selectedId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Compare with geofencesExit
        db.collection("geofencesExit")
                .whereEqualTo("uniqueID", selectedId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        alertName = documentSnapshot.getString("alertName");
                        if (alertName != null) {
                            Toast.makeText(context, "Match found in geofencesExit. AlertName: " + alertName, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Match found in geofencesExit. AlertName: " + alertName);
                            return; // Match found, no need to check further
                        }
                    }

                    // If no match is found in geofencesExit
                    Toast.makeText(context, "No match found in geofencesExit", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error querying geofencesExit", Toast.LENGTH_SHORT).show();
                });
    }



    public java.lang.String getAlertName() {
        return alertName;
    }
}
