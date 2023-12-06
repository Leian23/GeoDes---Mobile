package com.example.geodes_mobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.map_home;
import com.example.geodes_mobile.main_app.schedule_settings_adaptor.Adapter4;
import com.example.geodes_mobile.main_app.schedule_settings_adaptor.DataModel4;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment implements Adapter4.OnItemClickListener {
    private static final String TAG = "ScheduleFragment";
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_schedules, container, false);

        db = FirebaseFirestore.getInstance();

        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        ImageButton addButton = rootView.findViewById(R.id.btnAdd);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        fetchDataFromFirestore(rootView);

        menuButton.setOnClickListener(view -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        addButton.setOnClickListener(view -> {
            getParentFragmentManager().beginTransaction().hide(ScheduleFragment.this).commit();
            ((map_home) requireActivity()).hideElements(true);
            ((map_home) requireActivity()).BottomSheetAddSched();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchDataFromFirestore(rootView);
            swipeRefreshLayout.setRefreshing(false);
        });

        return rootView;
    }

    private void fetchDataFromFirestore(View rootView) {
        db.collection("geofenceSchedule")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DataModel4> data = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String schedTitle = document.getString("Sched");
                            String clock = document.getString("Time");
                            String UniqueId = document.getString("uniqueID");
                            String schedAlarms = document.getString("SchedAlarms");
                            Boolean isAlertSwitchOn = document.getBoolean("SchedStat");

                            boolean Monday = document.getBoolean("Monday");
                            boolean Tuesday = document.getBoolean("Tuesday");
                            boolean Wednesday = document.getBoolean("Wednesday");
                            boolean Thursday = document.getBoolean("Thursday");
                            boolean Friday = document.getBoolean("Friday");
                            boolean Saturday = document.getBoolean("Saturday");
                            boolean Sunday = document.getBoolean("Sunday");

                            StringBuilder selectedDays = new StringBuilder();

                            if (Monday) {
                                selectedDays.append("Mon, ");
                            }
                            if (Tuesday) {
                                selectedDays.append("Tue, ");
                            }
                            if (Wednesday) {
                                selectedDays.append("Wed, ");
                            }
                            if (Thursday) {
                                selectedDays.append("Thu, ");
                            }
                            if (Friday) {
                                selectedDays.append("Fri, ");
                            }
                            if (Saturday) {
                                selectedDays.append("Sat, ");
                            }
                            if (Sunday) {
                                selectedDays.append("Sun, ");
                            }
                            String result = selectedDays.toString().trim();

                            List<String> selectedItemsIds = (List<String>) document.get("selectedItemsIds");

                            if (selectedItemsIds != null && !selectedItemsIds.isEmpty()) {
                                // Concatenate the elements of the list into a single string
                                StringBuilder stringBuilder = new StringBuilder();
                                for (String itemId : selectedItemsIds) {
                                    stringBuilder.append(itemId).append("\n");
                                }

                                // Remove the last newline character
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                }
                                int iconCal = R.drawable.calendar_ic;
                                int entryImage = R.drawable.alarm_ic;
                                int iconMarker = R.drawable.clock_ic;
                                data.add(new DataModel4(schedTitle, clock, schedAlarms, iconCal, entryImage, iconMarker, isAlertSwitchOn, UniqueId, result, selectedItemsIds));
                            }
                        }
                        updateAdapterWithData(data, rootView);
                    } else {
                        Toast.makeText(getContext(), "Error fetching data from Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapterWithData(List<DataModel4> data, View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.settingsSchedd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter4 adapter = new Adapter4(data, getContext());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(DataModel4 data) {
        getParentFragmentManager().beginTransaction().hide(this).commit();
        ((map_home) requireActivity()).hideElements(true);
        ((map_home) requireActivity()).ViewSched();
        ((map_home) requireActivity()).setLongPressEnabled(false);
    }
}
