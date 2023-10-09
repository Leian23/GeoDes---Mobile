package com.example.geodes_mobile.main_app.bottom_sheet_content.addingSchedule;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

import com.example.geodes_mobile.R;

import org.osmdroid.views.MapView;

public class add_sched_dialog extends Dialog {
    private MapView mapView;
    private CheckBox checkBoxRestaurants;
    private CheckBox checkBoxTerminals;
    private CheckBox checkBoxHotels;
    private CheckBox checkBoxMuseum;
    private CheckBox checkBoxParks;

    private SharedPreferences sharedPreferences;

    public add_sched_dialog(Context context) {
        super(context);
        sharedPreferences = context.getSharedPreferences("checkbox_state", Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alert_layout);


    }
}
