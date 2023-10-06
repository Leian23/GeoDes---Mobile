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
import com.example.geodes_mobile.main_app.alert_settings_adaptor.Adapter3;
import com.example.geodes_mobile.main_app.alert_settings_adaptor.DataModel3;

import java.util.ArrayList;
import java.util.List;

public class AlertsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_alerts, container, false);

        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);



        List<DataModel3> data = new ArrayList<>();
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));
        data.add(new DataModel3("Work", "23", "hello everyonejjlvjdsfkdsfkdsjflkdsjfkldsjfkdsfsldfdskfjsdfjsdklfdss", R.drawable.calendar_ic, R.drawable.get_in, R.drawable.pinalerts, true));




        RecyclerView recyclerView = rootView.findViewById(R.id.settingsAlert);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)); // Changed the LinearLayoutManager constructor
        Adapter3 adapter = new Adapter3(data, getContext());
        recyclerView.setAdapter(adapter);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        return rootView;
    }
}
