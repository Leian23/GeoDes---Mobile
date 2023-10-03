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

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<DataModel> dataList;
    private Context context;

    public Adapter(List<DataModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
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

        holder.distanceTextView.setText(data.getDistance());
        holder.alertTitleTextView.setText(data.getAlertTitle());
        holder.noteTextView.setText(data.getNote());
        holder.imageView.setImageResource(data.getImageResource());
        holder.imageView1.setImageResource(data.getImageResource1());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView distanceTextView;
        TextView alertTitleTextView;
        TextView noteTextView;
        ImageView imageView;
        ImageView imageView1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            distanceTextView = itemView.findViewById(R.id.distance);
            alertTitleTextView = itemView.findViewById(R.id.alertTitle);
            noteTextView = itemView.findViewById(R.id.note);
            imageView = itemView.findViewById(R.id.imageView2);
            imageView1 = itemView.findViewById(R.id.imageView6); // Corrected line
        }

    }
}
