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

        EditText userName = findViewById(R.id.userName);
        EditText userPass = findViewById(R.id.userPass);

        getbtnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uName = userName.getText().toString();
                String uPass = userName.getText().toString();

                if (uName.equalsIgnoreCase("admin") && uPass.equals("1234"))
                {
                    showMessage("Alert", "Correct! ");
                }
                else if (uName.equalsIgnoreCase("") || uPass.equals(""))
                {
                    showMessage("alert","incomplete credentials! ");
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