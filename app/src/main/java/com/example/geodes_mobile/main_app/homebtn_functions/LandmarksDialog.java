package com.example.geodes_mobile.main_app.homebtn_functions;




import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.geodes_mobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LandmarksDialog extends Dialog {
    private CheckBox checkBoxRestaurants;
    private CheckBox checkBoxTerminals;
    private CheckBox checkBoxHotels;
    private CheckBox checkBoxMuseum;
    private CheckBox checkBoxParks;

    private SharedPreferences sharedPreferences;
    private MapView mapView;

    public LandmarksDialog(Context context, MapView mapView) {
        super(context);
        this.mapView = mapView;
        sharedPreferences = context.getSharedPreferences("checkbox_state", Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landmarks_layout2);

        checkBoxRestaurants = findViewById(R.id.checkBoxRestaurants);
        checkBoxTerminals = findViewById(R.id.checkBoxTerminals);
        checkBoxHotels = findViewById(R.id.checkBoxHotels);
        checkBoxMuseum = findViewById(R.id.checkBoxMuseum);
        checkBoxParks = findViewById(R.id.checkBoxParks);

        checkBoxRestaurants.setChecked(sharedPreferences.getBoolean("fast food", false));
        checkBoxTerminals.setChecked(sharedPreferences.getBoolean("terminal", false));
        checkBoxHotels.setChecked(sharedPreferences.getBoolean("hotel", false));
        checkBoxMuseum.setChecked(sharedPreferences.getBoolean("museum", false));
        checkBoxParks.setChecked(sharedPreferences.getBoolean("park", false));



        // Add OnCheckedChangeListener for each checkbox (you can do this for the rest of the checkboxes too).
        checkBoxRestaurants.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("fast food", isChecked).apply();

                if (isChecked) {
                    fetchAndDisplayMarkers("fast food");
                    Toast.makeText(getContext(), "Restaurants checkbox checked", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getContext(), "Restaurants checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxTerminals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("terminal", isChecked).apply();

                if (isChecked) {
                    fetchAndDisplayMarkers("terminal");
                    Toast.makeText(getContext(), "Restaurants checkbox checked", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getContext(), "Restaurants checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxHotels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("hotel", isChecked).apply();

                if (isChecked) {
                    fetchAndDisplayMarkers("hotel");
                    Toast.makeText(getContext(), "Restaurants checkbox checked", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getContext(), "Restaurants checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxMuseum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("museum", isChecked).apply();

                if (isChecked) {
                    fetchAndDisplayMarkers("museum");
                    Toast.makeText(getContext(), "Restaurants checkbox checked", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getContext(), "Restaurants checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxParks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("park", isChecked).apply();

                if (isChecked) {
                    fetchAndDisplayMarkers("park");
                    Toast.makeText(getContext(), "Restaurants checkbox checked", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getContext(), "Restaurants checkbox unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });












    }

    private void fetchAndDisplayMarkers(String category) {
        new FetchMarkersTask(category).execute();
    }

    private class FetchMarkersTask extends AsyncTask<Void, Void, List<Marker>> {
        private String category;
        private String errorMessage; // Store error message

        public FetchMarkersTask(String category) {
            this.category = category;
        }

        @Override
        protected List<Marker> doInBackground(Void... params) {
            List<Marker> markers = new ArrayList<>();

            try {
                String overpassQuery = getOverpassQuery(category);
                String overpassBaseUrl = "https://overpass-api.de/api/interpreter?data=";

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                String url = overpassBaseUrl + overpassQuery;

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    errorMessage = "HTTP Request Error: " + response.code();
                } else {
                    String responseString = response.body().string();
                    if (responseString != null && !responseString.isEmpty()) {
                        List<Marker> parsedMarkers = parseOverpassResponse(responseString);
                        markers.addAll(parsedMarkers);
                    } else {
                        errorMessage = "Empty Response from API";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = "Network error or request failed: " + e.getMessage();
            }

            return markers;
        }

        @Override
        protected void onPostExecute(List<Marker> markers) {
            super.onPostExecute(markers);

            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            } else if (markers.isEmpty()) {
                Toast.makeText(getContext(), "No markers received from the API", Toast.LENGTH_SHORT).show();
            } else {
                for (Marker marker : markers) {
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    mapView.getOverlays().add(marker);

                    // Customize the marker for "Parks"
                    if (category.equals("park")) {
                        // Replace 'R.drawable.park_marker' with the actual resource ID of your park marker icon
                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.marker_loc); // Load your custom Bitmap here
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);

// Set the Drawable as the icon for the marker
                        marker.setIcon(drawable);

// Add the marker to the map
                        mapView.getOverlays().add(marker);
                        mapView.invalidate();


                    }
                }
                mapView.invalidate();
            }
        }
    }


    private String getOverpassQuery(String category) {
        return "[out:json];node[amenity=" + category + "];out;";
    }

    private List<Marker> parseOverpassResponse(String response) {
        List<Marker> markers = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(response);

            if (json.has("elements")) {
                JSONArray elementsArray = json.getJSONArray("elements");
                for (int i = 0; i < elementsArray.length(); i++) {
                    JSONObject elementJson = elementsArray.getJSONObject(i);
                    long id = elementJson.getLong("id");
                    double lat = elementJson.optDouble("lat", 0);
                    double lon = elementJson.optDouble("lon", 0);

                    if (lat != 0 && lon != 0) {
                        Marker marker = new Marker(mapView);
                        marker.setPosition(new GeoPoint(lat, lon));
                        marker.setTitle(getTagName(elementJson));
                        markers.add(marker);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return markers;
    }

    private String getTagName(JSONObject elementJson) throws JSONException {
        JSONObject tagsJson = elementJson.optJSONObject("tags");
        if (tagsJson != null) {
            return tagsJson.optString("name", "");
        }
        return "";
    }





}
