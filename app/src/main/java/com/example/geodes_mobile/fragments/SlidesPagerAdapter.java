package com.example.geodes_mobile.fragments;// SlidesPagerAdapter.java


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.geodes_mobile.R;

public class SlidesPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 3;  // Change this to the number of slides

    public SlidesPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        // Provide different images and descriptions for each slide
        switch (position) {
            case 0:
                return SlideFragment.newInstance(R.drawable.landmarks, "Description 1kfjgjgirgirjgijigjijgrijgrgrigir");
            case 1:
                return SlideFragment.newInstance(R.drawable.pinalerts, "Description 2");
            case 2:
                return SlideFragment.newInstance(org.osmdroid.wms.R.drawable.osm_ic_center_map, "Description 3");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
