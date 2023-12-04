package com.example.geodes_mobile.main_app.alert_settings_adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.map_home;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.util.GeoPoint;

import java.util.List;

public class Adapter3 extends RecyclerView.Adapter<Adapter3.ViewHolder> {
    private List<DataModel3> dataList;
    private Context context;
    private OnItemClickListener listener;
    private FirebaseFirestore firestore;

    private static map_home mapHome;

    public Adapter3(List<DataModel3> dataList, Context context, map_home mapHome) {
        this.dataList = dataList;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.mapHome = mapHome;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_item_settings, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, this); // Pass the Adapter instance to ViewHolder
        viewHolder.setupSwitchListener(); // Move setupSwitchListener outside onCreateViewHolder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel3 data = dataList.get(position);

        holder.titleTextView.setText(data.getAlertName());
        holder.notesTextView.setText(data.getNotesAlert());
        holder.distance.setText(data.getDistance());
        holder.updateSwitchState(data.getAlertEnabled());
        holder.alertStatus.setImageResource(data.getSetAlertStat());

        holder.itemView.setOnClickListener(view -> {
            Toast.makeText(context, "Clicked: " + data.getId(), Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onItemClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView notesTextView;
        TextView distance;
        Switch alertEnabled;
        ImageView alertStatus;
        Adapter3 adapter; // Reference to the Adapter instance

        public ViewHolder(@NonNull View itemView, Adapter3 adapter) {
            super(itemView);
            this.adapter = adapter;
            titleTextView = itemView.findViewById(R.id.TitleSetAlert);
            notesTextView = itemView.findViewById(R.id.NoteSetAlert);
            distance = itemView.findViewById(R.id.distanceLabel);
            alertEnabled = itemView.findViewById(R.id.AlertSwitch);
            alertStatus = itemView.findViewById(R.id.EntryImage);

        }

        public void updateSwitchState(boolean isChecked) {
            alertEnabled.setOnCheckedChangeListener(null); // Remove listener to avoid callback during updating
            alertEnabled.setChecked(isChecked);
            alertEnabled.setOnCheckedChangeListener((buttonView, isCheckedNew) -> {
                // Update the alertEnabled value in your local data
                DataModel3 data = adapter.dataList.get(getAdapterPosition());
                data.setAlertEnabled(isCheckedNew);

                if(isCheckedNew) {
                    GeoPoint Point = new GeoPoint(data.getLatitude(), data.getLongitude());

                    if (data.getEntryType()) {
                        mapHome.addGeofence(Point, data.getOuterRadius(), data.getOuterCode(), data.getAlertName(), true);
                        mapHome.addGeofence(Point, data.getOuterRadius(), data.getInnerCode(), data.getAlertName(), true);

                    } else {
                        mapHome.addGeofence(Point, data.getOuterRadius(), data.getOuterCode(), data.getOuterCode(),false);
                    }

                } else if (!isCheckedNew) {
                    if (data.getEntryType()) {
                        mapHome.clearGeo(data.getInnerCode(), data.getOuterCode());
                    } else {
                        mapHome.removeGeofence(data.getOuterCode());
                    }
                }
                // Save the updated state to Firestore or perform other actions
                adapter.updateAlertEnabledInFirestore(data.getId(), isCheckedNew, data.getEntryType());
            });
        }



        // Add this method to set up the Switch listener
        public void setupSwitchListener() {
            alertEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Update the alertEnabled value in your local data
                DataModel3 data = adapter.dataList.get(getAdapterPosition());
                data.setAlertEnabled(isChecked);
                // Save the updated state to Firestore or perform other actions
                adapter.updateAlertEnabledInFirestore(data.getId(), isChecked, data.getEntryType());
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DataModel3 data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Add this setData method to update the data in the adapter
    public void setData(List<DataModel3> newData) {
        dataList.clear();
        dataList.addAll(newData);
        notifyDataSetChanged();
    }

    private void updateAlertEnabledInFirestore(String alertName, boolean isEnabled, boolean entrytype) {
        updateAlertEnabledInCollection("geofencesEntry", alertName, isEnabled, entrytype);
        updateAlertEnabledInCollection("geofencesExit", alertName, isEnabled, entrytype);
    }

    private void updateAlertEnabledInCollection(String collectionName, String alertName, boolean isEnabled, boolean typeEntry) {
        firestore.collection(collectionName).document(alertName)
                .update("alertEnabled", isEnabled)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Toast.makeText(context, "Alert state updated in " + collectionName, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(context, "Error updating alert state in " + collectionName + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
