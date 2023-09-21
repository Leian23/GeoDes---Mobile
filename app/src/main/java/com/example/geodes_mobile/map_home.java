package com.example.geodes_mobile;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class map_home extends AppCompatActivity {
    private boolean isFirstButtonColor1 = true; // Initial state for the first button is color 1
    private boolean isSecondButtonColor1 = true; // Initial state for the second button is color 1
    private boolean isThirdButtonColor1 = true;
    private boolean isFourthButtonColor1 = true;
    private Button firstButton;
    private Button secondButton;
    private Button thirdButton;
    private Button fourthButton;

    private BottomSheetBehavior bottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maphome);

        firstButton = findViewById(R.id.colorChangingButton);
        secondButton = findViewById(R.id.colorChangingButton2);
        thirdButton = findViewById(R.id.colorChangingButton3);
        fourthButton = findViewById(R.id.colorChangingButton4);

        // Set the rounded button background with initial colors
        setRoundedButtonBackground(firstButton, R.color.white, R.color.green);
        setRoundedButtonBackground(secondButton, R.color.white, R.color.green);
        setRoundedButtonBackground(thirdButton, R.color.white, R.color.green);
        setRoundedButtonBackground(fourthButton, R.color.white, R.color.green);
        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(firstButton, isFirstButtonColor1);
                isFirstButtonColor1 = !isFirstButtonColor1;
            }
        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(secondButton, isSecondButtonColor1);
                isSecondButtonColor1 = !isSecondButtonColor1;
            }
        });

        thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(thirdButton, isThirdButtonColor1);
                isThirdButtonColor1 = !isThirdButtonColor1;
            }
        });

        fourthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(fourthButton, isFourthButtonColor1);
                isFourthButtonColor1 = !isFourthButtonColor1;
            }
        });



        //Bottom Sheet
        LinearLayout linearLayout = findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        int customHeight = getResources().getDimensionPixelSize(R.dimen.custom_height);

        bottomSheetBehavior.setPeekHeight(customHeight);

    }

    private void toggleButtonColor(Button button, boolean isColor1) {
        if (isColor1) {
            setRoundedButtonBackground(button, R.color.green, R.color.white);
        } else {
            setRoundedButtonBackground(button, R.color.white, R.color.green);
        }
    }

    private void setRoundedButtonBackground(Button button, int backgroundColor, int textColor) {
        GradientDrawable roundedDrawable = new GradientDrawable();
        roundedDrawable.setShape(GradientDrawable.RECTANGLE);
        roundedDrawable.setCornerRadius(100); // Adjust the radius as needed
        roundedDrawable.setColor(ContextCompat.getColor(this, backgroundColor));

        button.setBackground(roundedDrawable);
        button.setTextColor(ContextCompat.getColor(this, textColor));
    }



}
