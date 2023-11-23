package com.example.geodes_mobile.main_app.homebtn_functions;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.geodes_mobile.R;

import java.util.Calendar;
import java.util.Locale;

public class SchedEditDialog extends Dialog {

    private Activity activity;

    public SchedEditDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_sheet_sched_edit);

        TextView txtTimePicker = findViewById(R.id.EditTimePicker);
        txtTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        activity, // Pass the activity context here
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker view, int selectedHour, int selectedMinute) {
                                // Convert 24-hour format to 12-hour format
                                int hourOfDay = selectedHour % 12;
                                String amPm = (selectedHour >= 12) ? "PM" : "AM";

                                // Handle the selected time, e.g., update the TextView
                                txtTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, selectedMinute, amPm));
                            }
                        },
                        hour,
                        minute,
                        false // Set to false to use 12-hour format
                );

                timePickerDialog.show();
            }
        });

        getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner); // Create a shape resource with rounded corners
    }
}
