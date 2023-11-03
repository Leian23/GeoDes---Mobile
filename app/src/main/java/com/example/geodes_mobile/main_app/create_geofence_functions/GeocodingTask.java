package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.content.Context;
import android.location.Location;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingTask extends AsyncTask<Location, Void, String> {
    private Context context;

    public GeocodingTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Location... locations) {
        if (locations.length == 0) {
            return null;
        }

        Location location = locations[0];
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String city = null;

        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0) {
                city = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return city;
    }

    @Override
    protected void onPostExecute(String city) {
        if (city != null) {
            // Use the 'city' value to display or process the city information
            Toast.makeText(context, "You are in " + city, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "City not found", Toast.LENGTH_SHORT).show();
        }
    }
}
