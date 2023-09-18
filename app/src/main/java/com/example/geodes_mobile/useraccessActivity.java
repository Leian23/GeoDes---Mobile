package com.example.geodes_mobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class useraccessActivity extends AppCompatActivity {
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // You can add code here to handle the OK button click
                        dialog.dismiss();
                    }
                })
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useraccess);

        Button getbtnSign = findViewById(R.id.siButton);

        EditText uName = findViewById(R.id.userName);
        EditText uPass = findViewById(R.id.userPass);
        String userName = uName.getText().toString();
        String userPass = uPass.getText().toString();

        getbtnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName == "admin" && userPass == "1234")
                {
                    showMessage("Alert", "Correct! ");
                }
                else
                {
                    showMessage("Alert","Wrong!");
                }

            }
        });


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}