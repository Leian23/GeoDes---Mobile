package com.example.geodes_mobile.main_app.bottom_sheet_content.alerts_section;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;

import org.osmdroid.util.GeoPoint;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<DataModel> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public Adapter(List<DataModel> dataList, Context context, OnItemClickListener onItemClickListener) {
        this.dataList = dataList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(GeoPoint geoPoint);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView distanceTextView;
        TextView alertTitleTextView;
        TextView noteTextView;
        ImageView entryStat;
        ImageView imageView1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alertTitleTextView = itemView.findViewById(R.id.alertTitle);
            noteTextView = itemView.findViewById(R.id.note);
            entryStat = itemView.findViewById(R.id.imageView9);


            // Set click listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Handle item click
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                onItemClickListener.onItemClick(dataList.get(position).getGeoPoint());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel data = dataList.get(position);


        holder.alertTitleTextView.setText(data.getAlertTitle());
        holder.noteTextView.setText(data.getNote());
        holder.entryStat.setImageResource(data.getImageResource());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
