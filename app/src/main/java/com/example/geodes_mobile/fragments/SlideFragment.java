// SlideFragment.java
package com.example.geodes_mobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.geodes_mobile.R;

public class SlideFragment extends Fragment {
    private static final String ARG_IMAGE_RES_ID = "imageResId";
    private static final String ARG_DESCRIPTION = "description";

    public SlideFragment() {
        // Required empty public constructor
    }

    public static SlideFragment newInstance(int imageResId, String description) {
        SlideFragment fragment = new SlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slide, container, false);

        ImageView imageView = rootView.findViewById(R.id.imageView);
        TextView descriptionTextView = rootView.findViewById(R.id.descriptionTextView);

        Bundle args = getArguments();
        if (args != null) {
            imageView.setImageResource(args.getInt(ARG_IMAGE_RES_ID));
            descriptionTextView.setText(args.getString(ARG_DESCRIPTION));
        }

        return rootView;
    }
}
