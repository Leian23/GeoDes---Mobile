package com.example.geodes_mobile.main_app.homebtn_functions;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.bottom_sheet_content.adding_alerts_sched.Adapter5;
import com.example.geodes_mobile.main_app.bottom_sheet_content.adding_alerts_sched.DataModel5;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SchedEditDialog extends Dialog {

    private Activity activity;
    private boolean[] selectedDays = new boolean[7]; // Array to store selected days
    private Map<String, Object> geofenceData = new HashMap<>();

    private String itemId;

    private FirebaseAuth mAUth;

    private DocumentReference alertDocRef;

   private  EditText scheduleNameEdit;

   private TextView editTimePicker;




    private boolean monday = false;
    private boolean tuesday = false;
    private boolean wednesday = false;
    private boolean thursday = false;
    private boolean friday = false;
    private boolean saturday = false;
    private boolean sunday = false;


    public SchedEditDialog(Activity activity, String itemId) {
        super(activity);
        this.activity = activity;
        this.itemId = itemId;
        Arrays.fill(selectedDays, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_sheet_sched_edit);

       scheduleNameEdit = findViewById(R.id.ScheduleNameEdit);
       editTimePicker = findViewById(R.id.EditTimePicker);
        Button discardButton = findViewById(R.id.discardSchedule);
        Button saveButton = findViewById(R.id.ButtonSaveSched);
        GridLayout daysGridLayout = findViewById(R.id.daysGridLayout);





        editTimePicker.setOnClickListener(v -> showTimePickerDialog());

        discardButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            String updatedSchedTitle = scheduleNameEdit.getText().toString();
            String updatedTime = editTimePicker.getText().toString();
            RecyclerView recyclerViewSched = findViewById(R.id.scheds_recyclerViewedit);
            Adapter5 adapterSched = (Adapter5) recyclerViewSched.getAdapter();

            // Include the selected items' unique IDs in your Firestore data
            Set<String> selectedItemsIds = adapterSched.getSelectedItemsIds();
            if (!selectedItemsIds.isEmpty()) {
                geofenceData.put("selectedItemsIds", new ArrayList<>(selectedItemsIds));
            }

            boolean[] updatedDays = selectedDays.clone(); // Clone to avoid reference issues

            updateFirestoreSchedule(itemId, updatedSchedTitle, updatedTime, new ArrayList<>(selectedItemsIds), updatedDays);
        });


        mAUth = FirebaseAuth.getInstance();

        FirebaseUser scheduleEmail = mAUth.getCurrentUser();

        if (scheduleEmail != null) {

            CollectionReference geofenceEntryCollection = FirebaseFirestore.getInstance().collection("geofencesEntry");
            CollectionReference geofenceExitCollection = FirebaseFirestore.getInstance().collection("geofencesExit");

            List<DataModel5> dataList = new ArrayList<>();

            geofenceEntryCollection.whereEqualTo("email", scheduleEmail.getEmail()).get().addOnSuccessListener(entrySnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : entrySnapshots) {
                    String alertTitle = documentSnapshot.getString("alertName");
                    String unID = documentSnapshot.getString("uniqueID");
                    int imageResource = R.drawable.get_in;

                    // Create DataModel5 object and add it to the list
                    dataList.add(new DataModel5(alertTitle, imageResource, unID));
                }

                geofenceExitCollection.get().addOnSuccessListener(exitSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : exitSnapshots) {
                        String alertTitle = documentSnapshot.getString("alertName");
                        String unID = documentSnapshot.getString("uniqueID");
                        int imageResource = R.drawable.get_out;

                        dataList.add(new DataModel5(alertTitle, imageResource, unID));
                    }

                    RecyclerView recyclerViewSched = findViewById(R.id.scheds_recyclerViewedit);
                    recyclerViewSched.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

                    Adapter5 adapterSched = new Adapter5(dataList, getContext());
                    recyclerViewSched.setAdapter(adapterSched);

                }).addOnFailureListener(e -> {
                    // Handle errors
                });

            }).addOnFailureListener(e -> {
                // Handle errors
            });

        } else {

        }


        loadAlertDataFromFirestore();
        setUpDaysGridLayout(daysGridLayout);

        getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner);
    }


    private void loadAlertDataFromFirestore() {
        alertDocRef = FirebaseFirestore.getInstance().collection("geofenceSchedule").document(itemId);
        alertDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                scheduleNameEdit.setText(task.getResult().getString("Sched"));
                editTimePicker.setText(String.valueOf(task.getResult().getString("Time")));

            } else {
                Log.e("AlertEditDialog", "Error loading data from Firestore: " + task.getException());
            }
        });
    }











    private void showTimePickerDialog() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                activity,
                (view, selectedHour, selectedMinute) -> {
                    int hourOfDay = selectedHour % 12;
                    String amPm = (selectedHour >= 12) ? "PM" : "AM";
                    TextView editTimePicker = findViewById(R.id.EditTimePicker);
                    editTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, selectedMinute, amPm));
                },
                hour,
                minute,
                false
        );

        timePickerDialog.show();
    }

    private void setUpDaysGridLayout(GridLayout daysGridLayout) {

        for (int i = 0; i < daysGridLayout.getChildCount(); i++) {
            View child = daysGridLayout.getChildAt(i);
            if (child instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) child;

                toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (buttonView.getId() == R.id.MonToggle) {
                        selectedDays[0] = isChecked;
                    } else if (buttonView.getId() == R.id.TueToggle) {
                        selectedDays[1] = isChecked;
                    } else if (buttonView.getId() == R.id.WedToggle) {
                        selectedDays[2] = isChecked;
                    } else if (buttonView.getId() == R.id.ThuToggle) {
                        selectedDays[3] = isChecked;
                    } else if (buttonView.getId() == R.id.FriToggle) {
                        selectedDays[4] = isChecked;
                    } else if (buttonView.getId() == R.id.SatToggle) {
                        selectedDays[5] = isChecked;
                    } else if (buttonView.getId() == R.id.SunToggle) {
                        selectedDays[6] = isChecked;
                    }

                });
            }
        }
    }

    private void updateFirestoreSchedule(String scheduleId, String updatedSchedTitle, String updatedTime, List<String> updatedSelectedItemsIds, boolean[] updatedDays) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("Sched", updatedSchedTitle);
        updatedData.put("Time", updatedTime);
        updatedData.put("selectedItemsIds", updatedSelectedItemsIds);
        updatedData.put("Monday", updatedDays[0]);
        updatedData.put("Tuesday", updatedDays[1]);
        updatedData.put("Wednesday", updatedDays[2]);
        updatedData.put("Thursday", updatedDays[3]);
        updatedData.put("Friday", updatedDays[4]);
        updatedData.put("Saturday", updatedDays[5]);
        updatedData.put("Sunday", updatedDays[6]);

        db.collection("geofenceSchedule")
                .document(scheduleId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activity, "Schedule updated", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.e("SchedEditDialog", "Error updating schedule in Firestore: " + e);
                    Toast.makeText(activity, "Error updating schedule", Toast.LENGTH_SHORT).show();
                });
    }
}
