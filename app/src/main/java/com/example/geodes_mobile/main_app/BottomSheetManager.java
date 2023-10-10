package com.example.geodes_mobile.main_app;

import android.app.Activity;
import android.view.View;

import com.example.geodes_mobile.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class BottomSheetManager {

    private BottomSheetBehavior bottomSheetBehavior;
    private View changePosLayout;
    private Activity activity;

    public BottomSheetManager(Activity activity, View bottomSheetView, View changePosLayout) {
        this.activity = activity;
        this.changePosLayout = changePosLayout;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setHideable(false);
        int customHeight = activity.getResources().getDimensionPixelSize(R.dimen.custom_height);
        bottomSheetBehavior.setPeekHeight(customHeight);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                // Handle state changes if needed
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                int layoutHeight = changePosLayout.getHeight();
                int offset = (int) ((slideOffset * 0.90 * layoutHeight));
                changePosLayout.setTranslationY(-offset);
            }
        });
    }

    public void showBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void hideBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}
