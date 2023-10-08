package com.example.geodes_mobile.main_app.search_location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;

import java.util.ArrayList;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private ArrayList<String> data;

    public SearchResultsAdapter() {
        data = new ArrayList<>();
    }

    public void setData(ArrayList<String> newData) {
        data.clear();
        data.addAll(newData);
    }
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String result = data.get(position);
        holder.textViewResult.setText(result);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewResult = itemView.findViewById(R.id.textViewResult);
        }
    }
}