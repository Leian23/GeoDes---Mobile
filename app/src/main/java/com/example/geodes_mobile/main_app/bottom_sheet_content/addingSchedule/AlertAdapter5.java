package com.example.geodes_mobile.main_app.bottom_sheet_content.addingSchedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;

import java.util.List;

public class AlertAdapter5 extends RecyclerView.Adapter<AlertAdapter5.ViewHolder> {

    private List<AlertItem> alertItems;

    public AlertAdapter5(List<AlertItem> alertItems) {
        this.alertItems = alertItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chosen_alerts_sched, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertItem alertItem = alertItems.get(position);

        // Bind data to views in your item layout
        holder.alertNameTextView.setText(alertItem.getAlertName());
        holder.alertType.setImageResource(alertItem.getAlertType());
    }

    @Override
    public int getItemCount() {
        return alertItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView alertNameTextView;
        ImageView alertType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alertNameTextView = itemView.findViewById(R.id.alertNamee);
            alertType = itemView.findViewById(R.id.alertTypee);

        }
    }
}