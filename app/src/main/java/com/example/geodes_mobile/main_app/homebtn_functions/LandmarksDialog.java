package com.example.geodes_mobile.main_app.homebtn_functions;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LandmarksDialog extends Dialog {
    private CheckBox checkBoxRestaurants;
    private CheckBox checkBoxTerminals;
    private CheckBox checkBoxMalls;
    private CheckBox checkBoxMuseum;
    private CheckBox checkBoxParks;

    private SharedPreferences sharedPreferences;
    private MapView mapView;
    private HashMap<String, Integer> markerCounts;
    private int maxMarkersPerKeyword = 200; // Maximum markers for each keyword
    private String errorMessage;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private Location currentLocation; // Store your current location here
    private double maxDistanceInMeters = 5000; // Specify your desired radius in meters

    public LandmarksDialog(Context context, MapView mapView, Location currentLocation) {
        super(context);
        this.mapView = mapView;
        this.currentLocation = currentLocation;
        sharedPreferences = context.getSharedPreferences("checkbox_state", Context.MODE_PRIVATE);
        markerCounts = new HashMap<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landmarks_layout2);

        checkBoxRestaurants = findViewById(R.id.checkBoxRestaurants);
        checkBoxTerminals = findViewById(R.id.checkBoxTerminals);
        checkBoxMalls = findViewById(R.id.checkBoxMalls);
        checkBoxMuseum = findViewById(R.id.checkBoxMuseum);
        checkBoxParks = findViewById(R.id.checkBoxParks);

        checkBoxRestaurants.setChecked(sharedPreferences.getBoolean("Jollibee", false));
        checkBoxTerminals.setChecked(sharedPreferences.getBoolean("Terminal", false));
        checkBoxMalls.setChecked(sharedPreferences.getBoolean("Mall", false));
        checkBoxMuseum.setChecked(sharedPreferences.getBoolean("Museum", false));
        checkBoxParks.setChecked(sharedPreferences.getBoolean("Park", false));


        SeekBar seekBarDistance = findViewById(R.id.seekBarDistance);
        TextView distanceRangeTextView = findViewById(R.id.distanceRange);


        maxDistanceInMeters = sharedPreferences.getFloat("maxDistanceInMeters", 5000); // Set a default value if not found in SharedPreferences

        // Update the SeekBar's progress based on the maxDistanceInMeters
        int progress = (int) (maxDistanceInMeters / 5000); // Reverse calculation
        seekBarDistance.setProgress(progress);

        // Update the TextView to display the updated distance range
        String rangeText = String.format(Locale.getDefault(), "%.1f km", maxDistanceInMeters / 1000.0);
        distanceRangeTextView.setText(rangeText);


        // Set an OnSeekBarChangeListener
        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Calculate the distance in meters based on the seek bar's progress
                maxDistanceInMeters = progress * 5000; // 3 kilometers per unit
                // Update the TextView to display the updated distance range
                String rangeText = String.format(Locale.getDefault(), "%.1f km", maxDistanceInMeters / 1000.0);
                distanceRangeTextView.setText(rangeText);

                // Save the modified maxDistanceInMeters value to SharedPreferences
                sharedPreferences.edit().putFloat("maxDistanceInMeters", (float) maxDistanceInMeters).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle when the user starts touching the seek bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle when the user stops touching the seek bar
                int progress = seekBar.getProgress();
                sharedPreferences.edit().putInt("seekBarProgress", progress).apply();
            }
        });


        checkBoxRestaurants.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("Jollibee", isChecked).apply();
                if (isChecked) {
                    String restaurantKeywords = "Jollibee,Chowking,McDonald's";
                    fetchAndDisplayMarkers(restaurantKeywords);
                    Toast.makeText(getContext(), "Restaurants checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Clear the markers related to these keywords
                    List<String> keywordsToRemove = Arrays.asList("Jollibee", "Chowking", "McDonald's");
                    clearMarkersForKeywords(keywordsToRemove);
                }
            }
        });

        checkBoxTerminals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("Terminal", isChecked).apply();
                if (isChecked) {
                    String restaurantKeywords = "Tricycle Terminal, Jeepney Terminal, Bus Terminal";
                    fetchAndDisplayMarkers(restaurantKeywords);
                    Toast.makeText(getContext(), "Terminal checkbox checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Clear the markers related to these keywords
                    List<String> keywordsToRemove = Arrays.asList("Tricycle Terminal", "Jeepney Terminal", "Bus Terminal");
                    clearMarkersForKeywords(keywordsToRemove);
                }
            }
        });

        checkBoxMalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("Mall", isChecked).apply();
                if (isChecked) {

                    String restaurantKeywords = "SM";
                    fetchAndDisplayMarkers(restaurantKeywords);
                    Toast.makeText(getContext(), "Mall checkbox checked", Toast.LENGTH_SHORT).show();

                } else {
                    List<String> keywordsToRemove = Arrays.asList("SM");
                    clearMarkersForKeywords(keywordsToRemove);

                }
            }
        });

        checkBoxMuseum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("Museum", isChecked).apply();
                if (isChecked) {

                } else {
                    // Clear the markers related to these keywords

                }
            }
        });

        checkBoxParks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("Park", isChecked).apply();
                if (isChecked) {

                } else {
                    // Clear the markers related to these keywords

                }
            }
        });






    }

    private void fetchAndDisplayMarkers(String keywords) {
        String[] keywordArray = keywords.split(",");

        for (String keyword : keywordArray) {
            executorService.submit(new FetchMarkersTask(keyword));
        }
    }

    private class FetchMarkersTask implements Runnable {
        private String keywords;

        public FetchMarkersTask(String keywords) {
            this.keywords = keywords;
        }

        @Override
        public void run() {
            // Check if the marker count for this keyword has reached the limit
            if (markerCounts.containsKey(keywords) && markerCounts.get(keywords) >= maxMarkersPerKeyword) {
                return;
            }

            List<Marker> markers = new ArrayList<>();
            try {
                String overpassQuery = getOverpassQuery(keywords);
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
                        int currentMarkerCount = markerCounts.getOrDefault(keywords, 0);
                        int remainingMarkers = maxMarkersPerKeyword - currentMarkerCount;

                        for (Marker marker : parsedMarkers) {
                            if (isWithinRadius(marker.getPosition(), maxDistanceInMeters)) {
                                markers.add(marker);
                            }
                        }

                        currentMarkerCount += markers.size();
                        markerCounts.put(keywords, currentMarkerCount);
                    } else {
                        errorMessage = "Empty Response from API";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = "Network error or request failed: " + e.getMessage();
            }
            addMarkersToMap(markers, keywords);
        }
    }

    // Check if a GeoPoint is within a specified radius from the current location
    private boolean isWithinRadius(GeoPoint geoPoint, double radiusMeters) {
        Location markerLocation = new Location("MarkerLocation");
        markerLocation.setLatitude(geoPoint.getLatitude());
        markerLocation.setLongitude(geoPoint.getLongitude());

        float distance = currentLocation.distanceTo(markerLocation);
        return distance <= radiusMeters;
    }

    // Modify your addMarkersToMap method to run on the main thread
    private void addMarkersToMap(final List<Marker> markers, final String keywords) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (errorMessage != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                } else if (markers.isEmpty()) {
                    Toast.makeText(getContext(), "No markers received from the API", Toast.LENGTH_SHORT).show();
                } else {
                    for (Marker marker : markers) {
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        mapView.getOverlays().add(marker);

                        if (keywords.contains("Jollibee")) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.restaurant_marker);
                            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                            marker.setIcon(drawable);
                            mapView.getOverlays().add(marker);
                            mapView.invalidate();
                        }

                        if (keywords.contains("McDonald's")) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mcdo_marker);
                            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                            marker.setIcon(drawable);
                            mapView.getOverlays().add(marker);
                            mapView.invalidate();
                        }

                        if (keywords.contains("Chowking")) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.chowking_landmark);
                            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                            marker.setIcon(drawable);
                            mapView.getOverlays().add(marker);
                            mapView.invalidate();
                        }

                        if (keywords.contains("Tricycle Terminal")) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.tricycle_marker);
                            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                            marker.setIcon(drawable);
                            mapView.getOverlays().add(marker);
                            mapView.invalidate();
                        }

                        if (keywords.contains("Jeepney Terminal")) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.jeepney_marker);
                            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                            marker.setIcon(drawable);
                            mapView.getOverlays().add(marker);
                            mapView.invalidate();
                        }

                        if (keywords.contains("Bus Terminal")) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bus_marker);
                            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                            marker.setIcon(drawable);
                            mapView.getOverlays().add(marker);
                            mapView.invalidate();
                        }





                    }
                    mapView.invalidate();
                }
            }
        });
    }

    private String getOverpassQuery(String keywords) {
        return "[out:json][bbox:5.37,117.17,18.85,126.6];node[name~\"" + keywords + "\"];out;";
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

    private void clearMarkersForKeywords(List<String> keywords) {
        List<Marker> markersToRemove = new ArrayList<>();

        for (org.osmdroid.views.overlay.Overlay overlay : mapView.getOverlays()) {
            if (overlay instanceof Marker) {
                Marker marker = (Marker) overlay;
                String markerKeywords = marker.getTitle();

                if (markerKeywords != null) {
                    // Check if any of the keywords to remove is contained in the marker's title
                    for (String keywordToRemove : keywords) {
                        if (markerKeywords.toLowerCase().contains(keywordToRemove.toLowerCase())) {
                            markersToRemove.add(marker);
                            break; // No need to check this marker further
                        }
                    }
                }
            }
        }

        for (Marker marker : markersToRemove) {
            mapView.getOverlays().remove(marker);
        }

        mapView.invalidate();
    }
}