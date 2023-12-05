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
    private static final String PREF_RINGTONE = "ringtone";
    private static final String KEY_SELECTED_RINGTONE_URI = "selected_ringtone_uri";
    private static final int REQUEST_RINGTONE_PICKER = 1;

    private static final String PREF_ALARM_RINGTONE = "alarm_ringtone";
    private static final String KEY_SELECTED_ALARM_RINGTONE_URI = "selected_alarm_ringtone_uri";
    private static final int REQUEST_ALARM_RINGTONE_PICKER = 2;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());


        Preference ringtonePreference = findPreference(PREF_RINGTONE);

        if (ringtonePreference != null) {
            ringtonePreference.setOnPreferenceClickListener(preference -> {
                // Launch the ringtone picker
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, REQUEST_RINGTONE_PICKER);
                return true;
            });
        }

        Preference alarmRingtonePreference = findPreference(PREF_ALARM_RINGTONE);

        if (alarmRingtonePreference != null) {
            alarmRingtonePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, REQUEST_ALARM_RINGTONE_PICKER);
                return true;
            });
        }

        updateRingtonePreferenceSummary();
        updateAlarmRingtonePreferenceSummary();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(getResources().getColor(R.color.white));

            int leftPaddingInDp = -28; // adjust this value as needed
            int leftPaddingInPx = (int) (leftPaddingInDp * getResources().getDisplayMetrics().density);
            view.setPadding(leftPaddingInPx, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (requestCode == REQUEST_RINGTONE_PICKER) {
                savePreference(KEY_SELECTED_RINGTONE_URI, selectedUri);
                updateRingtonePreferenceSummary();
            } else if (requestCode == REQUEST_ALARM_RINGTONE_PICKER) {
                savePreference(KEY_SELECTED_ALARM_RINGTONE_URI, selectedUri);
                updateAlarmRingtonePreferenceSummary();
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
        Preference ringtonePreference = findPreference(PREF_RINGTONE);
        if (ringtonePreference != null) {
            updatePreferenceSummary(ringtonePreference, KEY_SELECTED_RINGTONE_URI, getString(R.string.ringtone_label));
        }
    }

    private void updateAlarmRingtonePreferenceSummary() {
        Preference alarmRingtonePreference = findPreference(PREF_ALARM_RINGTONE);
        if (alarmRingtonePreference != null) {
            updatePreferenceSummary(alarmRingtonePreference, KEY_SELECTED_ALARM_RINGTONE_URI, getString(R.string.alarm_ringtone_label));
        }
    }

    private void updatePreferenceSummary(Preference preference, String uriKey, String defaultSummary) {
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

    public Uri getSelectedAlarmRingtoneUri() {
        String uriString = sharedPreferences.getString(KEY_SELECTED_ALARM_RINGTONE_URI, null);
        return uriString != null ? Uri.parse(uriString) : null;
    }
}
