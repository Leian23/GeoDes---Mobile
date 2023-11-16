package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.app.ActivityCompat;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.MainActivity;
import com.example.geodes_mobile.main_app.map_home;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFunctionHandler {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private Context context;
    private MapView mapView;
    private IMapController mapController;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isGpsProviderEnabled = true;
    private boolean isLocationUpdatesInitialized = false;
    private Marker mapMarker;

    private GeofenceSetup geofenceSetup;
    private SeekBar outerSeekBar;
    private SeekBar innerSeekBar;
    private  TextView coordinates;
    private boolean isLongPressEnabled = true;
    private boolean isEntryMode = true;
    private static final String API_KEY = "ade995c254e64059a8a05234230611"; // Replace with your API key
    private int initialMaxLevelOuter = 50; // maximum level of outerseekbar
    private int initialMaxLevelInner = 10; // maximum level of innerseekbar

    private double currentOuterRadius;
    private double currentInnerRadius;
    private TextView innerLabel;
    private TextView outerLabel;



    public MapFunctionHandler(Context context, MapView mapView, TextView coordinates, SeekBar outerSeekBar, SeekBar innerSeekBar, TextView outerLabel, TextView innerLabel) {
        this.context = context;
        this.mapView = mapView;
        this.coordinates = coordinates;
        this.outerSeekBar = outerSeekBar;
        this.innerSeekBar = innerSeekBar;
        this.outerLabel = outerLabel;
        this.innerLabel = innerLabel;

        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));



        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint geoPoint) {
                if (isLongPressEnabled) {
                    dropPinOnMap(geoPoint);
                    return true;
                } else {
                    return false; // Long press is disabled
                }
            }
        });

        mapView.getOverlays().add(0, mapEventsOverlay);

        // Check and request location permissions at runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        }
        // Set up SeekBar listeners
        setupSeekBarListeners();
        updateInnerSeekBarState();
    }

    private void setupSeekBarListeners() {
        outerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double outerRadiusMeters = (progress / (double) initialMaxLevelOuter) * 8000.0; // Convert progress to meters

                // Limit the minimum outer radius to 0.5 km
                if (outerRadiusMeters < 300.0) {
                    outerRadiusMeters = 300.0;
                    seekBar.setProgress((int) ((outerRadiusMeters / 8000.0) * initialMaxLevelOuter));
                }

                currentOuterRadius = outerRadiusMeters; // Store the current outer radius

                // Update geofences with the calculated outer radius and the current inner radius
                updateGeofences(currentOuterRadius, currentInnerRadius);

                double outerRadiusKm = outerRadiusMeters / 1000.0;
                String rangeText = String.format(Locale.getDefault(), "%.1f km", outerRadiusKm);
                outerLabel.setText(rangeText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        innerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double innerRadiusMeters = (progress / (double) initialMaxLevelInner) * 300.0; // Convert progress to meters


                if (innerRadiusMeters < 200.0) {

                    if (isEntryMode) {
                        innerRadiusMeters = 200.0;
                    } else {
                        innerRadiusMeters = 0.0;
                    }


                    seekBar.setProgress((int) ((innerRadiusMeters / 300.0) * initialMaxLevelInner));
                }



                currentInnerRadius = innerRadiusMeters; // Store the current outer radius
                updateGeofences(currentOuterRadius, currentInnerRadius);

                double innerRadiusKM = innerRadiusMeters / 1000.0;
                String rangeText = String.format(Locale.getDefault(), "%.1f km", innerRadiusKM);
                innerLabel.setText(rangeText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        // Inside setupSeekBarListeners method
        ToggleButton toggleButton = ((map_home) context).findViewById(R.id.toggleButton);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEntryMode = isChecked;
            updateInnerSeekBarState();

            // Call the method to update the color of the outer geofence based on the toggle button state
            if (geofenceSetup != null) {
                geofenceSetup.updateOuterGeofenceColor(mapView.getContext(), !isChecked);
            }
        });
    }


    public void dropPinOnMap(GeoPoint geoPoint) {
        // Clear existing marker and geofences
        clearMarkerAndGeofences();

        // Add a new marker at the long-pressed location
        mapMarker = new Marker(mapView);
        mapMarker.setPosition(geoPoint);
        mapMarker.setVisible(false);
        mapView.getOverlays().add(mapMarker);

        if (geofenceSetup == null) {
            geofenceSetup = new GeofenceSetup(context, mapView);
        }

        double outerRadius = 2; // default outer radius seekbar progress
        double innerRadius = 8; // default inner radius seekbar progress

        double initialSavedOuterRadius = (outerRadius / (double) initialMaxLevelOuter) * 8000.0;
        double initialSavedInnerRadius = isEntryMode ? (innerRadius / (double) 10) * 300.0 : 0; // Set your desired default inner radius

        // Calculate the initial progress based on the initial saved outer radius
        int initialOuterProgress = (int) ((initialSavedOuterRadius / 8000.0) * initialMaxLevelOuter);
        int initialInnerProgress = (int) ((initialSavedInnerRadius / 300.0) * initialMaxLevelInner);

        outerSeekBar.setProgress(initialOuterProgress);
        innerSeekBar.setProgress(initialInnerProgress);

        geofenceSetup.addMarkerWithGeofences(mapView.getContext(), geoPoint.getLatitude(), geoPoint.getLongitude(), initialSavedOuterRadius, initialSavedInnerRadius);

        // Calculate bounding box
        BoundingBox boundingBox = calculateBoundingBox(geoPoint, initialSavedOuterRadius);

        // Animate the map to the bounding box
        mapView.zoomToBoundingBox(boundingBox, true);

        // Reset map orientation
        mapView.setMapOrientation(0);

        mapView.invalidate();

        // enable the bottom sheet for geofence configuration
        ((map_home) context).BottomSheetRadii();
        updateWeatherView(geoPoint);

        coordinates.setText(geoPoint.getLatitude() + "\n" + geoPoint.getLongitude());
        coordinates.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String coordinates = "Coordinates: " + geoPoint.getLatitude() + ", " + geoPoint.getLongitude();
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                Toast.makeText(v.getContext(), "Coordinates copied to clipboard", Toast.LENGTH_SHORT).show();
                ClipData clip = ClipData.newPlainText("Coordinates", coordinates.substring("Coordinates: ".length()));
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });

        // Set the toggle button to true
        ToggleButton toggleButton = ((map_home) context).findViewById(R.id.toggleButton);
        toggleButton.setChecked(true);
    }





    private BoundingBox calculateBoundingBox(GeoPoint center, double radius) {
        double halfDistanceInMeters = radius * 1.5; // Adjust as needed for better visibility
        double latPerMeter = 1.0 / 111319.9; // Approximate value for latitude degrees per meter

        double deltaLat = halfDistanceInMeters * latPerMeter * - 2; // Adjust to move the pin further to the top
        double deltaLon = halfDistanceInMeters / (111319.9 * Math.cos(Math.toRadians(center.getLatitude())));

        // Set latitude to the maximum latitude for the pin to be further at the top
        double pinLatitude = center.getLatitude() + deltaLat;

        double minLon = center.getLongitude() - deltaLon ; // Adjust to center the bounding box
        double maxLon = center.getLongitude() + deltaLon ; // Adjust to center the bounding box

        return new BoundingBox(pinLatitude, maxLon, center.getLatitude(), minLon);
    }
    

    public void clearMarkerAndGeofences() {
        // Remove existing marker and geofences
        mapView.getOverlays().remove(mapMarker);
        mapView.getOverlayManager().removeIf(overlay ->
                overlay instanceof Polygon || overlay instanceof Marker);

        mapView.invalidate();
    }


    private void updateGeofences(double outerRadius, double innerRadius) {
        if (geofenceSetup != null && mapMarker != null) {
            GeoPoint markerPosition = mapMarker.getPosition();

            // Assuming the geofences are stored in MapManager, update them directly
            geofenceSetup.updateGeofences(markerPosition, outerRadius, innerRadius);

            // Calculate bounding box
            BoundingBox boundingBox = calculateBoundingBox(markerPosition, outerRadius);

            // Animate the map to the new bounding box
                mapView.zoomToBoundingBox(boundingBox, true);

            mapView.invalidate();
        }
    }


    // Inside MapFunctionHandler class
    private void updateInnerSeekBarState() {
        if (isEntryMode) {
            innerSeekBar.setEnabled(true);
            double initialSavedInnerRadius = isEntryMode ? (10 / (double) 10) * 300.0 : 0;
            int initialInnerProgress = (int) ((initialSavedInnerRadius / 300.0) * 10);
            innerSeekBar.setProgress(initialInnerProgress);
        } else {
            innerSeekBar.setEnabled(false);
            innerSeekBar.setProgress(0);
        }
    }

    public void setLongPressEnabled(boolean enabled) {
        isLongPressEnabled = enabled;
    }


    private void updateWeatherView(GeoPoint geoPoint) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=" + geoPoint.getLatitude() + "," + geoPoint.getLongitude();
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JSONObject weatherData = new JSONObject(responseData);

                    if (weatherData.has("current")) {
                        JSONObject currentData = weatherData.getJSONObject("current");
                        double temperatureCelsius = currentData.getDouble("temp_c");

                        JSONObject conditionObject = currentData.getJSONObject("condition");
                        String conditionText = conditionObject.getString("text");

                        int iconCode = 0;

                        OkHttpClient conditionsClient = new OkHttpClient();
                        String apiUrl1 = "https://www.weatherapi.com/docs/weather_conditions.json";
                        Request request1 = new Request.Builder()
                                .url(apiUrl1)
                                .build();

                        Response response1 = conditionsClient.newCall(request1).execute();

                        if (response1.isSuccessful()) {
                            String responseData1 = response1.body().string();
                            JSONArray weatherData1 = new JSONArray(responseData1);

                            for (int i = 0; i < weatherData1.length(); i++) {
                                JSONObject condition = weatherData1.getJSONObject(i);
                                String daytime = condition.getString("day");
                                String nighttime = condition.getString("night");

                                if (conditionText.equals(daytime) || conditionText.equals(nighttime)) {
                                    iconCode = condition.getInt("icon");
                                    break; // Exit the loop once a match is found
                                }
                            }
                        }

                        // Determine if it's day or night
                        boolean isDay = isDaytime((geoPoint.getLatitude()), geoPoint.getLongitude());

                        // Construct the URL for the weather icon based on the icon code
                        String iconUrl = "https://cdn.weatherapi.com/weather/64x64/" + (isDay ? "day" : "night") + "/" + iconCode + ".png";

                        // Update the WeatherView with weather information and load the icon URL
                        ((Activity) context).runOnUiThread(() -> {
                            View weatherView = ((map_home) context).findViewById(R.id.WeatherView);
                            TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp);
                            TextView conditionTextView = weatherView.findViewById(R.id.weatherCon);
                            ImageView iconImageView = weatherView.findViewById(R.id.weatherIconImageView);

                            temperatureTextView.setText(String.format("%.1f °C", temperatureCelsius));
                            conditionTextView.setText(conditionText);

                            // Load the weather icon using Picasso
                            Picasso.get().load(iconUrl).into(iconImageView);
                        });

                    } else {
                        ((Activity) context).runOnUiThread(() -> {
                            Toast.makeText(context, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity) context).runOnUiThread(() -> {
                    View weatherView = ((map_home) context).findViewById(R.id.WeatherView);
                    TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp);
                    temperatureTextView.setText(e.getMessage());
                });
            }
        }).start();
    }

    private static boolean isDaytime(double latitude, double longitude) {
        try {
            OkHttpClient client = new OkHttpClient();
            String apiUrl = "https://api.sunrise-sunset.org/json?lat=" + latitude + "&lng=" + longitude;
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                JSONObject sunriseSunsetData = new JSONObject(responseData);

                if (sunriseSunsetData.has("results")) {
                    JSONObject results = sunriseSunsetData.getJSONObject("results");
                    String sunrise = results.getString("sunrise");
                    String sunset = results.getString("sunset");

                    // Get the current time
                    int currentHour = java.time.LocalTime.now().getHour();
                    int currentMinute = java.time.LocalTime.now().getMinute();

                    // Parse sunrise and sunset times
                    int sunriseHour = Integer.parseInt(sunrise.split(":")[0]);
                    int sunriseMinute = Integer.parseInt(sunrise.split(":")[1]);
                    int sunsetHour = Integer.parseInt(sunset.split(":")[0]);
                    int sunsetMinute = Integer.parseInt(sunset.split(":")[1]);

                    // Check if the current time is between sunrise and sunset
                    return (currentHour > sunriseHour || (currentHour == sunriseHour && currentMinute >= sunriseMinute))
                            && (currentHour < sunsetHour || (currentHour == sunsetHour && currentMinute < sunsetMinute));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Default to considering it as daytime in case of errors
        return true;
    }


}
