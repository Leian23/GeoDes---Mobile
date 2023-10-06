// Adapter4.java

package com.example.geodes_mobile.main_app.schedule_settings_adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;

import java.util.List;

public class Adapter4 extends RecyclerView.Adapter<Adapter4.ViewHolder> {
    private List<DataModel4> dataList;
    private Context context;

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
        holder.distanceTextView.setText(data.getDistance());
        holder.noteTextView.setText(data.getNote());
        holder.iconCalImageView.setImageResource(data.getIconCal());
        holder.entryImageImageView.setImageResource(data.getEntryImage());
        holder.iconMarkerImageView.setImageResource(data.getIconMarker());
        holder.alertSwitch.setChecked(data.isAlertSwitchOn());
        // Populate other views as needed

        // You may need to handle onClickListeners for switches or other interactive elements
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView distanceTextView;
        TextView noteTextView;
        ImageView iconCalImageView;
        ImageView entryImageImageView;
        ImageView iconMarkerImageView;
        Switch alertSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.TitleSetSched);
            distanceTextView = itemView.findViewById(R.id.TimeSched);
            noteTextView = itemView.findViewById(R.id.AlertDesc);
            iconCalImageView = itemView.findViewById(R.id.calendarImage);
            entryImageImageView = itemView.findViewById(R.id.AlarmIc);
            iconMarkerImageView = itemView.findViewById(R.id.clockSchedIc);
            alertSwitch = itemView.findViewById(R.id.AlertSwitch);
            // Initialize other views as needed
        }
    }
}
