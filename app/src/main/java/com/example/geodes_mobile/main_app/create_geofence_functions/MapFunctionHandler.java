package com.example.geodes_mobile.main_app.create_geofence_functions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.app.ActivityCompat;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.MainActivity;
import com.example.geodes_mobile.main_app.map_home;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFunctionHandler {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final long MIN_TIME_BETWEEN_UPDATES = 0; // 500 milliseconds
    private static final float MIN_DISTANCE_BETWEEN_UPDATES = 10;

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
    private boolean isLongPressEnabled = true;
    private boolean isEntryMode = true;
    private static final String WEATHER_API_BASE_URL = "http://api.weatherapi.com/v1"; // Replace with your API base URL
    private static final String API_KEY = "ade995c254e64059a8a05234230611"; // Replace with your API key






    public MapFunctionHandler(Context context, MapView mapView, SeekBar outerSeekBar, SeekBar innerSeekBar) {
        this.context = context;
        this.mapView = mapView;
        this.outerSeekBar = outerSeekBar;
        this.innerSeekBar = innerSeekBar;

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
                double outerRadius = progress + 0;
                updateGeofences(outerRadius, innerSeekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            // Other methods...
        });

        innerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double innerRadius = progress + 0;
                updateGeofences(outerSeekBar.getProgress(), innerRadius);
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
                geofenceSetup.updateOuterGeofenceColor(!isChecked);
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

        double outerRadius = 50; // Set your desired default outer radius
        double innerRadius = isEntryMode ? 20 : 0; // Set your desired default inner radius

        outerSeekBar.setProgress((int) outerRadius);
        innerSeekBar.setProgress((int) innerRadius);

        geofenceSetup.addMarkerWithGeofences(mapView.getContext(), geoPoint.getLatitude(), geoPoint.getLongitude(), outerRadius, innerRadius);

        // Calculate bounding box
        BoundingBox boundingBox = calculateBoundingBox(geoPoint, outerRadius);

        // Animate the map to the bounding box
        mapView.zoomToBoundingBox(boundingBox, true);

        // Reset map orientation
        mapView.setMapOrientation(0);

        mapView.invalidate();



        // enable the bottomsheet for geofence configuration
        ((map_home) context).BottomSheetRadii();
        updateWeatherView(geoPoint);

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
            innerSeekBar.setProgress(20);
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
                        String conditionText = currentData.getJSONObject("condition").getString("text");

                        // Update the Weatherview with weather information
                        ((Activity) context).runOnUiThread(() -> {
                            View weatherView = ((map_home) context).findViewById(R.id.WeatherView);
                            TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp);
                            TextView conditionTextView = weatherView.findViewById(R.id.weatherCon);

                            temperatureTextView.setText(String.format("%.1f °C", temperatureCelsius));
                            conditionTextView.setText(conditionText);
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





}
