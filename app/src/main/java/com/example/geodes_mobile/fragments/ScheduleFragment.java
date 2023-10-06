package com.example.geodes_mobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.schedule_settings_adaptor.Adapter4;
import com.example.geodes_mobile.main_app.schedule_settings_adaptor.DataModel4;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_schedules, container, false);


        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);





        List<DataModel4> data = new ArrayList<>();


        data.add(new DataModel4("School", "9:00pm", "Alert#1, Alert#2", R.drawable.calendar_ic, R.drawable.alarm_ic, R.drawable.clock_ic, true));
        data.add(new DataModel4("School", "9:00pm", "Alert#1, Alert#2", R.drawable.calendar_ic, R.drawable.alarm_ic, R.drawable.clock_ic, true));
        data.add(new DataModel4("School", "9:00pm", "Alert#1, Alert#2", R.drawable.calendar_ic, R.drawable.alarm_ic, R.drawable.clock_ic, true));
        data.add(new DataModel4("School", "9:00pm", "Alert#1, Alert#2", R.drawable.calendar_ic, R.drawable.alarm_ic, R.drawable.clock_ic, true));
        data.add(new DataModel4("School", "9:00pm", "Alert#1, Alert#2", R.drawable.calendar_ic, R.drawable.alarm_ic, R.drawable.clock_ic, true));

        RecyclerView recyclerView = rootView.findViewById(R.id.settingsSchedd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)); // Changed the LinearLayoutManager constructor
        Adapter4 adapter = new Adapter4(data, getContext());
        recyclerView.setAdapter(adapter);





        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        return rootView;
    }
}
