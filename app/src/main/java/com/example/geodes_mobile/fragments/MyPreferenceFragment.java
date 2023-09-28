package com.example.geodes_mobile.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.geodes_mobile.R;

public class MyPreferenceFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_RINGTONE_PICKER = 1;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Find the custom ringtone preference
        Preference ringtonePreference = findPreference("ringtone");

        // Set the click listener for the custom ringtone preference
        if (ringtonePreference != null) {
            ringtonePreference.setOnPreferenceClickListener(preference -> {
                // Launch the ringtone picker
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, REQUEST_RINGTONE_PICKER);
                return true;
            });
        }
        updateRingtonePreferenceSummary();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            // Save the selected URI in SharedPreferences
            if (requestCode == REQUEST_RINGTONE_PICKER) {
                savePreference("selected_ringtone_uri", selectedUri);
                updateRingtonePreferenceSummary();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void savePreference(String key, Uri value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value != null ? value.toString() : null);
        editor.apply();
    }

    private void updateRingtonePreferenceSummary() {
        Preference ringtonePreference = findPreference("ringtone");
        if (ringtonePreference != null) {
            updatePreferenceSummary(ringtonePreference, "selected_ringtone_uri", "Ringtone");
        }
    }

    private void updatePreferenceSummary(Preference preference, String uriKey, String defaultSummary) {
        // Retrieve the saved URI
        String uriString = sharedPreferences.getString(uriKey, null);

        if (preference != null) {
            if (uriString != null) {
                Uri uri = Uri.parse(uriString);
                String name = getRingtoneName(uri);
                preference.setSummary((name != null ? name : "None"));
            } else {
                preference.setSummary("Select custom " + defaultSummary.toLowerCase());
            }
        }
    }

    private String getRingtoneName(Uri ringtoneUri) {
        if (ringtoneUri == null) {
            return null;
        }

        Ringtone ringtone = RingtoneManager.getRingtone(requireContext(), ringtoneUri);
        if (ringtone != null) {
            return ringtone.getTitle(requireContext());
        }

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Set the background color here
        if (view != null) {
            view.setBackgroundColor(getResources().getColor(R.color.white));

            int leftPaddingInDp = -28; // adjust this value as needed
            int leftPaddingInPx = (int) (leftPaddingInDp * getResources().getDisplayMetrics().density);
            view.setPadding(leftPaddingInPx, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }

        return view;
    }
}
