package com.example.geodes_mobile.main_app.homebtn_functions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.example.geodes_mobile.R;

public class AlertEditDialog extends Dialog {

    private Context context;

    public AlertEditDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_sheet_alert_edit);

        // Initialize views

        // Set up click listener for the save button


        getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner); // Create a shape resource with rounded corners
    }
}
