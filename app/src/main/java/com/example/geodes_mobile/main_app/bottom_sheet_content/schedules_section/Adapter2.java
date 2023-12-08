package com.example.geodes_mobile.main_app.bottom_sheet_content.schedules_section;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;

import java.util.List;

public class Adapter2 extends RecyclerView.Adapter<Adapter2.ViewHolder> {
    private List<DataModel2> dataList;
    private Context context;

    public Adapter2(List<DataModel2> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel2 data = dataList.get(position);

        holder.schedTitleTextView.setText(data.getSchedTitle());
        holder.timeTextView.setText(data.getTime());
        holder.repeatTimeTextView.setText(data.getRepeatTime());
        holder.imageView.setImageResource(data.getSchedImage());
        holder.calendarImageView.setImageResource(data.getCalendarImage());
        holder.alarmIconImageView.setImageResource(data.getAlarmIcon());

        // Check if selectedItemsIds is not null before joining
        if (data.getSelectedItemsIds() != null) {
            String selectedItemsText = TextUtils.join(", ", data.getSelectedItemsIds());
            holder.alertListsTextView.setText(selectedItemsText);
        } else {
            holder.alertListsTextView.setText("");
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView schedTitleTextView;
        TextView timeTextView;
        TextView repeatTimeTextView;
        ImageView imageView;
        ImageView calendarImageView;
        ImageView alarmIconImageView;
        TextView alertListsTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            schedTitleTextView = itemView.findViewById(R.id.SchedTitle);
            timeTextView = itemView.findViewById(R.id.time);
            repeatTimeTextView = itemView.findViewById(R.id.repeat_time);
            imageView = itemView.findViewById(R.id.schedImage);
            calendarImageView = itemView.findViewById(R.id.calendarImage);
            alarmIconImageView = itemView.findViewById(R.id.alarm);
            alertListsTextView = itemView.findViewById(R.id.alertlists);

        }
    }
}
