package com.example.geodes_mobile.main_app.bottom_sheet_content.adding_alerts_sched;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Adapter5 extends RecyclerView.Adapter<Adapter5.ViewHolder> {
    private List<DataModel5> dataList;
    private Context context;



    public Adapter5(List<DataModel5> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_alert_from_sched, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel5 data = dataList.get(position);

        holder.alertTitleTextView.setText(data.getAlertTitle());
        holder.imageView.setImageResource(data.getImageResource());

        // Set the background color based on selection
        holder.itemView.setBackgroundColor(data.getSelectedIds().isEmpty() ? Color.TRANSPARENT : Color.LTGRAY);

        holder.itemView.setOnClickListener(v -> {
            data.toggleSelected(data.getUnid()); // Replace with the actual method to get unique ID
            notifyDataSetChanged(); // Refresh the view
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView alertTitleTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.alertType1);
            alertTitleTextView = itemView.findViewById(R.id.alertnameList);
        }
    }

    // Expose a method to get the selected items' unique IDs
    public Set<String> getSelectedItemsIds() {
        Set<String> selectedIds = new HashSet<>();
        for (DataModel5 data : dataList) {
            if (!data.getSelectedIds().isEmpty()) {
                selectedIds.addAll(data.getSelectedIds());
            }
        }
        return selectedIds;
    }
}
