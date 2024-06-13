package com.example.geodes_mobile.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.geodes_mobile.main_app.homebtn_functions.SchedEditDialog;
import com.example.geodes_mobile.main_app.map_home;
import com.example.geodes_mobile.main_app.schedule_settings_adaptor.Adapter4;
import com.example.geodes_mobile.main_app.schedule_settings_adaptor.DataModel4;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ScheduleFragment extends Fragment implements Adapter4.OnItemClickListener {
    private static final String TAG = "ScheduleFragment";
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Context context;


    private String concatenatedString;

    private FirebaseFirestore firestore;

    private View rootView;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragments_schedules, container, false);

        db = FirebaseFirestore.getInstance();

        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        ImageButton addButton = rootView.findViewById(R.id.btnAdd);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        context = getContext();


        firestore = FirebaseFirestore.getInstance();
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("geofenceSchedule")
                .whereEqualTo("Email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DataModel4> data = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Boolean isSchedEnabled = document.getBoolean("SchedStat");
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
                                StringBuilder stringBuilder = new StringBuilder();
                                for (String itemId : selectedItemsIds) {
                                    stringBuilder.append(itemId).append("\n");
                                }

                                if (stringBuilder.length() > 0) {
                                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                }

                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                List<String> concatenatedAlertNames = new ArrayList<>();

                                List<Task<QuerySnapshot>> tasks = new ArrayList<>();

                                for (String id : selectedItemsIds) {
                                    Task<QuerySnapshot> entryTask = db.collection("geofencesEntry")
                                            .whereEqualTo("uniqueID", id)
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    String alertName = documentSnapshot.getString("alertName");
                                                    if (alertName != null) {
                                                        Log.d(TAG, "Match found in geofencesEntry. AlertName: " + alertName);
                                                        concatenatedAlertNames.add(alertName);

                                                        // Capture the correct reference here
                                                        DocumentReference entryDocumentRef = documentSnapshot.getReference();


                                                        if (isSchedEnabled && isTimeAndDayMatch(document)) {

                                                            entryDocumentRef.update("alertEnabled", true)
                                                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Alert enabled: " + entryDocumentRef.getId()))
                                                                    .addOnFailureListener(e -> Log.e(TAG, "Error enabling alert: " + entryDocumentRef.getId(), e));
                                                        }
                                                        return;
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure if needed
                                            });

                                    tasks.add(entryTask);

                                    Task<QuerySnapshot> exitTask = db.collection("geofencesExit")
                                            .whereIn("uniqueID", Collections.singletonList(id))
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshotsExit -> {
                                                for (DocumentSnapshot documentSnapshotExit : queryDocumentSnapshotsExit) {
                                                    String alertNameExit = documentSnapshotExit.getString("alertName");
                                                    if (alertNameExit != null) {
                                                        Log.d(TAG, "Match found in geofencesExit. AlertName: " + alertNameExit);
                                                        concatenatedAlertNames.add(alertNameExit);

                                                        if (isSchedEnabled && isTimeAndDayMatch(document)) {
                                                            document.getReference().update("alertEnabled", true)
                                                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Alert enabled: " + document.getId()))
                                                                    .addOnFailureListener(e -> Log.e(TAG, "Error enabling alert: " + document.getId(), e));
                                                        }
                                                        return;
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure if needed
                                            });

                                    tasks.add(exitTask);
                                }

                                Tasks.whenAllComplete(tasks)
                                        .addOnSuccessListener(voids -> {
                                            concatenatedString = concatenatedAlertNames.toString();
                                            concatenatedString = concatenatedString.substring(1, concatenatedString.length() - 1);
                                            Log.d(TAG, "Concatenated Alert Names: " + concatenatedString);
                                            int iconCal = R.drawable.calendar_ic;
                                            int entryImage = R.drawable.alarm_ic;
                                            int iconMarker = R.drawable.clock_ic;
                                            data.add(new DataModel4(schedTitle, clock, schedAlarms, iconCal, entryImage, iconMarker, isAlertSwitchOn, UniqueId, result, selectedItemsIds, concatenatedString));
                                            updateAdapterWithData(data, rootView);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure if needed
                                        });

                            }
                        }

                        // Update the adapter with the new data
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

    private boolean isTimeAndDayMatch(QueryDocumentSnapshot document) {
        String alertTimeString = document.getString("Time");

        if (alertTimeString != null) {

            try {
                // Parse the time string into hours and minutes
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                int alertHour = sdf.parse(alertTimeString).getHours();
                int alertMinute = sdf.parse(alertTimeString).getMinutes();

                // Get the current time
                Calendar currentTime = Calendar.getInstance();
                int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                int currentMinute = currentTime.get(Calendar.MINUTE);

                Log.d(TAG, "Current Time: " + getFormattedTime12Hour(currentTime));
                Log.d(TAG, "Current Day: " + getCurrentDay(currentTime));
                Log.d(TAG, "Alert Time: " + getFormattedTime12Hour(alertHour, alertMinute));
                Log.d(TAG, "Alert Day: " + getAlertDay(document));

                //alertDetails
                String TimeOfAlertHour = getFormattedTime12Hour(alertHour, alertMinute);
                String DayOfAlert = getAlertDay(document);

                //CurrentDetails

                String CurrentTimeHour =  getFormattedTime12Hour(currentTime);
                String CurrentDay = getAlertDay(document);


                // Compare only the hours, minutes, and days
                if (CurrentTimeHour.equalsIgnoreCase(TimeOfAlertHour) &&
                        DayOfAlert.equalsIgnoreCase(CurrentDay) ) {

                    return true;
                }


            } catch (ParseException e) {
                Log.e(TAG, "Error parsing time: " + alertTimeString, e);
            }
        }

        // Return false if time string is null or parsing fails
        Log.d(TAG, "isTimeAndDayMatch: false (Time string is null or parsing fails)");
        return false;
    }

    private String getFormattedTime12Hour(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String getCurrentDay(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String getFormattedTime12Hour(int hour, int minute) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return sdf.format(calendar.getTime());
    }



    private String getAlertDay(QueryDocumentSnapshot document) {
        // Determine the selected day from the document
        if (document.getBoolean("Monday")) {
            return "Monday";
        } else if (document.getBoolean("Tuesday")) {
            return "Tuesday";
        } else if (document.getBoolean("Wednesday")) {
            return "Wednesday";
        } else if (document.getBoolean("Thursday")) {
            return "Thursday";
        } else if (document.getBoolean("Friday")) {
            return "Friday";
        } else if (document.getBoolean("Saturday")) {
            return "Saturday";
        } else if (document.getBoolean("Sunday")) {
            return "Sunday";
        }

        return "Unknown";
    }


    private boolean isDayMatch(int currentDayOfWeek, boolean... selectedDays) {
        // Example: If the alert is set for Monday (selectedDays[0]), and the current day is Monday (currentDayOfWeek == 2), return true
        return selectedDays[currentDayOfWeek - 1];
    }

    @Override
    public void onItemClick(DataModel4 data) {
        getParentFragmentManager().beginTransaction().hide(this).commit();
        ((map_home) requireActivity()).hideElements(true);
        ((map_home) requireActivity()).ViewSched();
        ((map_home) requireActivity()).setLongPressEnabled(false);

        ((map_home) requireActivity()).SchedTitle.setText(data.getSchedTitle());
        ((map_home) requireActivity()).schedRep.setText(data.getSchedules());
        ((map_home) requireActivity()).SchedStart.setText(data.getTimeStart());
        ((map_home) requireActivity()).alarmList.setText(data.getAlarmList());

        ImageButton editSched = ((map_home) requireActivity()).editSchedButton.findViewById(R.id.EditSchedule);
        editSched.setOnClickListener(view -> {
            // Log statement indicating that the ImageButton was tapped
            Log.d("ImageButton", "EditAlertIcon tapped");

            SchedEditDialog schedDialog = new SchedEditDialog(getActivity(), data.getUniqueId());
            schedDialog.show();
        });

        ImageButton deleteASched = ((map_home) requireActivity()).deleteSchedule.findViewById(R.id.DeleteSchedule);
        deleteASched.setOnClickListener(view -> {
            // Log statement indicating that the ImageButton was tapped
            Log.d("ImageButton", "DeleteAlert1 tapped");

            showConfirmationDialog(data.getUniqueId());
        });
    }




    private void deleteAlertFromFirestore(String alertId) {
        // Get the reference to the document you want to delete
        DocumentReference entryAlertDocRef = firestore.collection("geofenceSchedule").document(alertId);

        // Use a batch write to delete the document atomically
        firestore.runBatch(batch -> {
                    batch.delete(entryAlertDocRef);
                })
                .addOnSuccessListener(aVoid -> {
                    fetchDataFromFirestore(rootView);


                    View viewAlert1 = requireActivity().findViewById(R.id.viewSchedule);
                    if (viewAlert1 != null) {
                        viewAlert1.setVisibility(View.GONE);
                    }

                    Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                    if (currentFragment != null) {
                        requireActivity().getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
                    }

                    ((map_home) requireActivity()).setLongPressEnabled(true);
                    ((map_home) requireActivity()).showElements();



                })
                .addOnFailureListener(e -> {
                    Log.e("AlertsFragment", "Error deleting alert from Firestore: " + e.getMessage());
                    Toast.makeText(context, "Failed to delete alert", Toast.LENGTH_SHORT).show();
                });
    }

    private void showConfirmationDialog(String alertId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this alert?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // User clicked Delete, proceed with deletion
                    deleteAlertFromFirestore(alertId);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User clicked Cancel, do nothing
                    dialog.dismiss();
                })
                .show();
    }


}