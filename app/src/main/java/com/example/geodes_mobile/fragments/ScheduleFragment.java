package com.example.geodes_mobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.map_home;
import com.example.geodes_mobile.main_app.schedule_settings_adaptor.Adapter4;
import com.example.geodes_mobile.main_app.schedule_settings_adaptor.DataModel4;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment implements Adapter4.OnItemClickListener {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_schedules, container, false);

        FragmentManager fragmentManager = getParentFragmentManager();  // Use getParentFragmentManager() instead of getSupportFragmentManager()

        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        ImageButton addButton = rootView.findViewById(R.id.btnAdd);

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        List<DataModel4> data = new ArrayList<>();

        data.add(new DataModel4("School", "9:00pm", "Alert#1, Alert#2", R.drawable.calendar_ic, R.drawable.alarm_ic, R.drawable.clock_ic, true));

        RecyclerView recyclerView = rootView.findViewById(R.id.settingsSchedd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter4 adapter = new Adapter4(data, getContext());
        adapter.setOnItemClickListener(this); // Set the listener
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assuming you want to hide the current fragment
                getParentFragmentManager().beginTransaction().hide(ScheduleFragment.this).commit();

                // Assuming map_home has a hideElements method
                ((map_home) requireActivity()).hideElements(true);

                // Assuming map_home has a BottomSheetAddSched method
                ((map_home) requireActivity()).BottomSheetAddSched();
            }
        });
        return rootView;
    }


    //ito yung function pag pinindot yung item na galing sa adapter
    @Override
    public void onItemClick(DataModel4 data) {
        getParentFragmentManager().beginTransaction().hide(this).commit();
        ((map_home) requireActivity()).hideElements(true);

        ((map_home) requireActivity()).ViewSched();
        ((map_home) requireActivity()).setLongPressEnabled(false);
    }
}
