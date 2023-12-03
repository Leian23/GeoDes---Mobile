package com.example.geodes_mobile.main_app.homebtn_functions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.geodes_mobile.R;

public class AlertEditDialog extends Dialog {

    private Context context;
    private Button discardButton;

    public AlertEditDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottom_sheet_alert_edit);

        discardButton = findViewById(R.id.discardEditAlert);

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the current dialog
                dismiss();
            }
        });







        getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner); // Create a shape resource with rounded corners
    }
}
