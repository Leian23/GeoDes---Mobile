package com.example.geodes_mobile.main_app.bottom_sheet_content.adding_alerts_sched;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chosen_alerts_sched, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel5 data = dataList.get(position);

        holder.alertTitleTextView.setText(data.getAlertTitle());
        holder.imageView.setImageResource(data.getImageResource());
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
            alertTitleTextView = itemView.findViewById(R.id.alertTitle);
            imageView = itemView.findViewById(R.id.alertType);

        }
    }
}
