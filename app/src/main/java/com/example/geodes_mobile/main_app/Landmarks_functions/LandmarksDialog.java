package com.example.geodes_mobile.main_app.Landmarks_functions;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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

    private SharedPreferences sharedPreferences;

    public LandmarksDialog(Context context) {
        super(context);
        sharedPreferences = context.getSharedPreferences("checkbox_state", Context.MODE_PRIVATE);
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

        // Restore the state of the checkboxes from SharedPreferences
        checkBoxRestaurants.setChecked(sharedPreferences.getBoolean("restaurants", false));
        checkBoxTerminals.setChecked(sharedPreferences.getBoolean("terminals", false));
        checkBoxHotels.setChecked(sharedPreferences.getBoolean("hotels", false));
        checkBoxMuseum.setChecked(sharedPreferences.getBoolean("museum", false));
        checkBoxParks.setChecked(sharedPreferences.getBoolean("parks", false));

        // Set an OnCheckedChangeListener for each CheckBox to save their state
        checkBoxRestaurants.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the Restaurants checkbox
                sharedPreferences.edit().putBoolean("restaurants", isChecked).apply();

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
                // Save the state of the Terminals checkbox
                sharedPreferences.edit().putBoolean("terminals", isChecked).apply();

                if (isChecked) {
                    // Perform some action when Terminals checkbox is checked
                    Toast.makeText(getContext(), "Terminals checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Terminals checkbox is unchecked
                    Toast.makeText(getContext(), "Terminals checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxHotels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the Hotels checkbox
                sharedPreferences.edit().putBoolean("hotels", isChecked).apply();

                if (isChecked) {
                    // Perform some action when Hotels checkbox is checked
                    Toast.makeText(getContext(), "Hotels checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Hotels checkbox is unchecked
                    Toast.makeText(getContext(), "Hotels checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxMuseum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the Museum checkbox
                sharedPreferences.edit().putBoolean("museum", isChecked).apply();

                if (isChecked) {
                    // Perform some action when Museum checkbox is checked
                    Toast.makeText(getContext(), "Museum checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Museum checkbox is unchecked
                    Toast.makeText(getContext(), "Museum checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxParks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the Parks checkbox
                sharedPreferences.edit().putBoolean("parks", isChecked).apply();

                if (isChecked) {
                    // Perform some action when Parks checkbox is checked
                    Toast.makeText(getContext(), "Parks checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform some action when Parks checkbox is unchecked
                    Toast.makeText(getContext(), "Parks checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
