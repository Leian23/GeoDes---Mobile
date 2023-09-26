package com.example.geodes_mobile.main_app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.geodes_mobile.R;

public class LandmarksDialog extends Dialog {

    private CheckBox checkBoxRestaurants;
    private CheckBox checkBoxTerminals;
    private CheckBox checkBoxHotels;
    private CheckBox checkBoxMuseum;
    private CheckBox checkBoxParks;

    public LandmarksDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landmarks_layout);

        checkBoxRestaurants = findViewById(R.id.checkBoxRestaurants);
        checkBoxTerminals = findViewById(R.id.checkBoxTerminals);
        checkBoxHotels = findViewById(R.id.checkBoxHotels);
        checkBoxMuseum = findViewById(R.id.checkBoxMuseum);
        checkBoxParks = findViewById(R.id.checkBoxParks);

        // Set an OnCheckedChangeListener for each CheckBox
        checkBoxRestaurants.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Perform some action when Restaurants checkbox is checked
                    Toast.makeText(getContext(), "Restaurants checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Restaurants checkbox is unchecked
                    Toast.makeText(getContext(), "Restaurants checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxTerminals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Perform some action when Restaurants checkbox is checked
                    Toast.makeText(getContext(), "Terminals checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Restaurants checkbox is unchecked
                    Toast.makeText(getContext(), "Termimnals checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxHotels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Perform some action when Restaurants checkbox is checked
                    Toast.makeText(getContext(), "Hotels checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Restaurants checkbox is unchecked
                    Toast.makeText(getContext(), "Hotels checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxMuseum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Perform some action when Restaurants checkbox is checked
                    Toast.makeText(getContext(), "Museum checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Restaurants checkbox is unchecked
                    Toast.makeText(getContext(), "Museum checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxParks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Perform some action when Restaurants checkbox is checked
                    Toast.makeText(getContext(), "Parks checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Restaurants checkbox is unchecked
                    Toast.makeText(getContext(), "Parks checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
