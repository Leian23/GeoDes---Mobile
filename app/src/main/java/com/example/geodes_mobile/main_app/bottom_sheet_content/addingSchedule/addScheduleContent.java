package com.example.geodes_mobile.main_app.bottom_sheet_content.addingSchedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geodes_mobile.R;

import java.util.Calendar;

public class addScheduleContent extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_sched);  // Replace 'your_layout' with the layout XML file for this activity

        // Assuming txtTimePicker is in the layout associated with this activity
        final TextView txtTimePicker = findViewById(R.id.txtTimePicker);

        txtTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        addScheduleContent.this,  // Use 'addScheduleContent.this' instead of 'map_home.this'
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker view, int selectedHour, int selectedMinute) {
                                // Handle the selected time, e.g., update the TextView
                                txtTimePicker.setText(selectedHour + ":" + selectedMinute);
                            }
                        },
                        hour,
                        minute,
                        true
                );

                timePickerDialog.show();
            }
        });
    }
}
