package com.example.geodes_mobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.alert_settings_adaptor.Adapter3;
import com.example.geodes_mobile.main_app.alert_settings_adaptor.DataModel3;
import com.example.geodes_mobile.main_app.create_geofence_functions.MapFunctionHandler;
import com.example.geodes_mobile.main_app.map_home;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlertsFragment extends Fragment implements Adapter3.OnItemClickListener {

    private Adapter3 adapter;
    private FirebaseFirestore firestore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final double EARTH_RADIUS = 6371;
    private map_home mainActivity;
    private org.osmdroid.util.GeoPoint Point;
    private String alertName;
    private MapFunctionHandler locationhandler;

    private static final String API_KEY = "ade995c254e64059a8a05234230611";

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_alerts, container, false);

        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        mainActivity = (map_home) requireActivity();
        context = getContext();

        List<DataModel3> data = new ArrayList<>();

        // Setup SwipeRefreshLayout
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadAlerts);

        firestore = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = rootView.findViewById(R.id.settingsAlert);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new Adapter3(data, getContext());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        menuButton.setOnClickListener(view -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        loadAlerts();

        return rootView;
    }

    @Override
    public void onItemClick(DataModel3 data) {
        getParentFragmentManager().beginTransaction().hide(this).commit();
        ((map_home) requireActivity()).hideElements(true);
        ((map_home) requireActivity()).ViewAlerts();
        ((map_home) requireActivity()).setLongPressEnabled(false);

        // Accessing the textView from map_home activity and setting its text
        String alertName = data.getAlertName();
        Log.d("AlertsFragment", "Selected Alert Name: " + alertName);


        String latText = data.getLatitude().toString();
        String longText = data.getLongitude().toString();

        final String coordinates = "Coordinates: " + latText + ", " + longText;
        GeoPoint point = new GeoPoint(data.getLatitude(), data.getLongitude());

        ((map_home) requireActivity()).ViewAlerttitle.setText(alertName);

        ((map_home) requireActivity()).coordinatesValueAlertView.setText(data.getLatitude() + "\n" + data.getLongitude());
        ((map_home) requireActivity()). notesView.setText(data.getNotesAlert());


        ((map_home) requireActivity()). notesView.setText(data.getNotesAlert());


        RelativeLayout layout = ((map_home) requireActivity()).findViewById(R.id.infoLayout1);
        layout.setVisibility(View.GONE);

        FrameLayout layout1 = ((map_home) requireActivity()).findViewById(R.id.idLoad1);
        layout1.setVisibility(View.VISIBLE);

        FrameLayout layoutt = ((map_home) requireActivity()).findViewById(R.id.NotAvail1);

      //  ((map_home) requireActivity()).mapView.getController().animateTo(point);


        if(data.getAlertEnabled()) {
            BoundingBox boundingBox = calculateBoundingBox(point, data.getOuterRadius());
            ((map_home) requireActivity()).mapView.setMapOrientation(0);
            ((map_home) requireActivity()).mapView.zoomToBoundingBox(boundingBox, true);

            calculateBoundingBox(point, data.getOuterRadius());
        } else {
            BoundingBox boundingBox = calculateBoundingBox(point, data.getOuterRadius());
            ((map_home) requireActivity()).mapView.setMapOrientation(0);
            ((map_home) requireActivity()).mapView.zoomToBoundingBox(boundingBox, true);

            calculateBoundingBox(point, data.getOuterRadius());

        }

        layoutt.setVisibility(View.GONE);
        updateWeatherView(point);

    }




    private void loadAlerts() {
        // Fetch data from "geofencesEntry"
        Query entryQuery = firestore.collection("geofencesEntry");
        Query exitQuery = firestore.collection("geofencesExit");

        Task<QuerySnapshot> entryTask = entryQuery.get();
        Task<QuerySnapshot> exitTask = exitQuery.get();

        Tasks.whenAllSuccess(entryTask, exitTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DataModel3> data = new ArrayList<>();

                        QuerySnapshot entrySnapshot = (QuerySnapshot) task.getResult().get(0);
                        QuerySnapshot exitSnapshot = (QuerySnapshot) task.getResult().get(1);

                        processSnapshot(data, entrySnapshot);
                        processSnapshot(data, exitSnapshot);

                        adapter.setData(data);
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        // Handle errors
                        Toast.makeText(getContext(), "Error loading alerts: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processSnapshot(List<DataModel3> data, QuerySnapshot snapshot) {
        for (DocumentChange documentChange : snapshot.getDocumentChanges()) {
            alertName = documentChange.getDocument().getString("alertName");
            String alertNotes = documentChange.getDocument().getString("notes");
            Boolean alertEnabled = documentChange.getDocument().getBoolean("alertEnabled");
            Boolean alertStat = documentChange.getDocument().getBoolean("EntryType");
            String uniID = documentChange.getDocument().getString("uniqueID");
            Double outerRad = documentChange.getDocument().getDouble("outerRadius");

            Location currentLocation = mainActivity.getCurrentLocation();
            Map<String, Object> location = (Map<String, Object>) documentChange.getDocument().get("location");

            if (location != null) {
                double userLa = currentLocation.getLatitude();
                double userlo = currentLocation.getLongitude();

                double latitude = (double) location.get("latitude");
                double longitude = (double) location.get("longitude");

                double distanceToAlert = calculateDistance(userLa, userlo, latitude, longitude);

                String computedDistance = String.valueOf(distanceToAlert);

                Point = new org.osmdroid.util.GeoPoint(latitude, longitude);

                int alerstatus;

                if (alertStat) {
                    alerstatus = R.drawable.get_in;
                } else {
                    alerstatus = R.drawable.get_out;
                }

                data.add(new DataModel3(alertName, alertNotes, computedDistance, alertEnabled, uniID, alerstatus, latitude, longitude, outerRad));

            }
        }
    }

    public static double calculateDistance(double userLat, double userLng, Double targetLat, Double targetLng) {
        if (targetLat == null || targetLng == null) {
            return Double.MAX_VALUE;
        }

        double lat1 = Math.toRadians(userLat);
        double lon1 = Math.toRadians(userLng);
        double lat2 = Math.toRadians(targetLat);
        double lon2 = Math.toRadians(targetLng);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }


    public void updateWeatherView(GeoPoint geoPoint) {
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
                            View weatherView = ((map_home) context).findViewById(R.id.WeatherView1);
                            TextView temperatureTextView = weatherView.findViewById(R.id.weatherTemp1);
                            TextView conditionTextView = weatherView.findViewById(R.id.weatherCon1);
                            ImageView iconImageView = weatherView.findViewById(R.id.weatherIconImageView1);
                            RelativeLayout layout = weatherView.findViewById(R.id.infoLayout1);
                            FrameLayout layout1 = weatherView.findViewById(R.id.idLoad1);
                            FrameLayout layout2 = weatherView.findViewById(R.id.NotAvail1);
                            layout1.setVisibility(View.VISIBLE);

                            temperatureTextView.setText(String.format("%.1f °C", temperatureCelsius));
                            conditionTextView.setText(conditionText);
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.GONE);
                            Picasso.get().load(iconUrl).into(iconImageView);
                            layout.setVisibility(View.VISIBLE);
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
        return true;
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




}
