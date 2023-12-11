package com.example.geodes_mobile.main_app.homebtn_functions;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.main_app.create_geofence_functions.MapFunctionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
    private MapView mapView;
    private CheckBox[] checkBoxes;
    private SharedPreferences sharedPreferences;
    private HashMap<String, Integer> markerCounts = new HashMap<>();
    private int maxMarkersPerKeyword = 250;
    private String errorMessage;

    private ExecutorService executorService = Executors.newFixedThreadPool(32);
    private Location currentLocation;
    private double maxDistanceInMeters = 5000;
    private MapFunctionHandler mapFunctionHandler; // Add this member variable
    private List<CheckBox> selectedCheckboxes = new ArrayList<>();


    public LandmarksDialog(Context context, MapView mapView, Location currentLocation, MapFunctionHandler mapFunctionHandler) {
        super(context);
        this.mapView = mapView;
        this.currentLocation = currentLocation;
        this.mapFunctionHandler = mapFunctionHandler; // Initialize the MapFunctionHandler reference
        sharedPreferences = context.getSharedPreferences("checkbox_state", Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landmarks_layout2);

        checkBoxes = new CheckBox[] {
                findViewById(R.id.checkBoxRestaurants),
                findViewById(R.id.checkBoxTerminals),
                findViewById(R.id.checkBoxMalls),
                findViewById(R.id.checkBoxConvenience),
                findViewById(R.id.checkBoxParks)
        };

        setupCheckBoxes();
        setupSeekBar();
    }

    private void setupCheckBoxes() {
        String[] keywords = new String[] {
                "Jollibee,Chowking,McDonald's",
                "LRT,Tricycle Terminal,Bus Terminal,Jeepney Terminal,Bus Station,JAC Liner,JAM Liner,Victory Liner",
                "Shangrila Mall,Vista Mall,Mall of Asia,SM Store,SM City,Robinsons Place,Robinsons,Robinsons Galleria,Savemore",
                "FamilyMart,7-Eleven,Uncle John's",
                "Park"
        };

        String[] prefsKeys = new String[] {
                "Fast food",
                "Terminal",
                "Mall",
                "Station",
                "Park"
        };

        for (int i = 0; i < checkBoxes.length; i++) {
            CheckBox checkBox = checkBoxes[i];
            String keywordsString = keywords[i];
            String prefKey = prefsKeys[i];

            boolean isChecked = sharedPreferences.getBoolean(prefKey, false);

            // Check if markers exist for the current keyword
            boolean markersExist = markersExistForKeywords(keywordsString.split(","));
            if (!markersExist) {
                // If no markers exist, uncheck the checkbox
                isChecked = false;
            }

            checkBox.setChecked(isChecked);

            final int finalI = i;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sharedPreferences.edit().putBoolean(prefsKeys[finalI], isChecked).apply();

                    if (isChecked) {
                        if (selectedCheckboxes.size() >= 2) {
                            // Limit to 2 checkboxes, uncheck the first one in the list
                            CheckBox firstSelected = selectedCheckboxes.get(0);
                            firstSelected.setChecked(false);
                            selectedCheckboxes.remove(0);
                        }
                        selectedCheckboxes.add(checkBox);

                        fetchAndDisplayMarkers(keywords[finalI]);
                        Toast.makeText(getContext(), checkBox.getText() + " checkbox checked", Toast.LENGTH_SHORT).show();
                    } else {
                        selectedCheckboxes.remove(checkBox);
                        clearMarkersForKeywords(keywords[finalI].split(","));
                    }
                }
            });
        }
    }


    private void setupSeekBar() {
        SeekBar seekBarDistance = findViewById(R.id.seekBarDistance);
        TextView distanceRangeTextView = findViewById(R.id.distanceRange);

        int progress = sharedPreferences.getInt("seekBarProgress", 0); // Get the stored progress value
        maxDistanceInMeters = progress * 5000; // Calculate maxDistanceInMeters based on progress

        // Set the text to the current progress
        String rangeText = String.format(Locale.getDefault(), "%.1f km", maxDistanceInMeters / 1000.0);
        distanceRangeTextView.setText(rangeText);

        seekBarDistance.setProgress(progress);

        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxDistanceInMeters = progress * 5000;
                // Update the text as the progress changes
                String rangeText = String.format(Locale.getDefault(), "%.1f km", maxDistanceInMeters / 1000.0);
                distanceRangeTextView.setText(rangeText);
                sharedPreferences.edit().putFloat("maxDistanceInMeters", (float) maxDistanceInMeters).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                sharedPreferences.edit().putInt("seekBarProgress", progress).apply();
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

    private boolean isWithinRadius(GeoPoint geoPoint, double radiusMeters) {
        Location markerLocation = new Location("MarkerLocation");
        markerLocation.setLatitude(geoPoint.getLatitude());
        markerLocation.setLongitude(geoPoint.getLongitude());

        float distance = currentLocation.distanceTo(markerLocation);
        return distance <= radiusMeters;
    }

    private void addMarkersToMap(final List<Marker> markers, final String keywords) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (errorMessage != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                } else if (markers.isEmpty()) {
                    Toast.makeText(getContext(), "No markers received from the API"+ keywords, Toast.LENGTH_SHORT).show();
                } else {
                    for (Marker marker : markers) {
                        setMarkerIconByKeyword(marker, keywords);

                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                String markerName = marker.getTitle();

                                if (markerName != null && !markerName.isEmpty()) {
                                    // Create a custom dialog content view
                                    View markerInfoView = getLayoutInflater().inflate(R.layout.dialog_marker, null);

                                    GeoPoint markerPosition = marker.getPosition();

                                    // Animate the map view to the marker's position (center of the screen)
                                    mapView.getController().animateTo(markerPosition);

                                    // Find the TextViews in the custom dialog content view
                                    TextView markerNameTextView = markerInfoView.findViewById(R.id.markerNameTextView);
                                    TextView coordinatesTextView = markerInfoView.findViewById(R.id.coordinatesTextView);
                                    TextView addressTextView = markerInfoView.findViewById(R.id.addressTextView); // Add an TextView for the address

                                    // Set the marker name and coordinates in the TextViews
                                    markerNameTextView.setText(markerName);
                                    final String coordinates = "Coordinates: " + marker.getPosition().getLatitude() + ", " + marker.getPosition().getLongitude();
                                    coordinatesTextView.setText(coordinates);

                                    // Retrieve the address for the marker's coordinates and display it
                                    new GetAddressTask(addressTextView).execute(marker.getPosition());

                                    // Create a PopupWindow with custom content view
                                    final PopupWindow popupWindow = new PopupWindow(markerInfoView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                                    // Set the gravity for the PopupWindow to align it to the bottom-left corner
                                    popupWindow.showAtLocation(mapView, Gravity.START | Gravity.BOTTOM, 20, 500); // Adjust the y offset (e.g., -100)

                                    // Add a long-press listener to the markerInfoView to copy the coordinates
                                    markerInfoView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            // Copy the plain coordinates to the clipboard
                                            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clip = ClipData.newPlainText("Coordinates", coordinates.substring("Coordinates: ".length()));
                                            clipboard.setPrimaryClip(clip);
                                            Toast.makeText(getContext(), "Coordinates copied to clipboard", Toast.LENGTH_SHORT).show();
                                            return true;
                                        }
                                    });

                                    markerInfoView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // Call the dropPinOnMap method from the MapFunctionHandler
                                            if (mapFunctionHandler != null) {
                                                mapFunctionHandler.dropPinOnMap(marker.getPosition());
                                            }

                                            Toast.makeText(getContext(), "You have clicked the dialog box", Toast.LENGTH_SHORT).show();
                                            popupWindow.dismiss();
                                        }
                                    });
                                }
                                return true; // Return true to consume the event, false to allow the default behavior
                            }
                        });

                        mapView.getOverlays().add(marker);
                        mapView.invalidate();
                    }
                }
            }
        });
    }

    private void setMarkerIconByKeyword(Marker marker, String keywords) {
        int resourceId = 0;
        String[] keywordArray = keywords.split(",");

        for (String keyword : keywordArray) {
            String trimmedKeyword = keyword.trim(); // Trim leading/trailing spaces
            if (marker.getTitle() != null && marker.getTitle().toLowerCase().contains(trimmedKeyword.toLowerCase())) {
                switch (trimmedKeyword.toLowerCase()) { // Convert the keyword to lowercase for case-insensitive comparison
                    case "jollibee":
                        resourceId = R.drawable.restaurant_marker;
                        break;
                    case "mcdonald's":
                        resourceId = R.drawable.mcdo_marker;
                        break;
                    case "chowking":
                        resourceId = R.drawable.chowking_landmark;
                        break;
                    case "tricycle terminal":
                    case "tricycle transport terminal":
                    case "tricycle transportation terminal":
                        resourceId = R.drawable.tricycle_marker;
                        break;
                    case "jeepney terminal":
                        resourceId = R.drawable.jeepney_marker;
                        break;
                    case "bus terminal":
                    case "bus station":
                    case "jac liner":
                    case "jam liner":
                    case "victory liner":
                        resourceId = R.drawable.bus_marker;
                        break;
                    case "lrt":
                        resourceId = R.drawable.train_station;
                        break;
                    case "mall of asia":
                    case "sm store":
                    case "sm city":
                        resourceId = R.drawable.sm_markers;
                        break;
                    case "savemore":
                        resourceId = R.drawable.save_more_marker;
                        break;
                    case "robinsons place":
                    case "robinsons galleria":
                    case "robinsons":
                        resourceId = R.drawable.robinson_marker;
                        break;
                    case "shangrila mall":
                    case "vista mall":
                        resourceId = R.drawable.other_malls;
                        break;
                    case "uncle john's":
                        resourceId = R.drawable.ministop_marker;
                        break;
                    case "7-eleven":
                        resourceId = R.drawable.eleven_marker;
                        break;
                    case "familymart":
                        resourceId = R.drawable.family_mart_marker;
                        break;
                    case "Park":
                        
                        break;
                    default:
                        resourceId = 0;
                        break;
                }
                if (resourceId != 0) {
                    break; // No need to check further
                }
            }
        }

        if (resourceId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), resourceId);
            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
            marker.setIcon(drawable);
        }
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

    private void clearMarkersForKeywords(String[] keywords) {
        List<Marker> markersToRemove = new ArrayList<>();

        for (org.osmdroid.views.overlay.Overlay overlay : mapView.getOverlays()) {
            if (overlay instanceof Marker) {
                Marker marker = (Marker) overlay;
                String markerKeywords = marker.getTitle();

                if (markerKeywords != null) {
                    for (String keywordToRemove : keywords) {
                        if (markerKeywords.toLowerCase().contains(keywordToRemove.toLowerCase())) {
                            markersToRemove.add(marker);
                            break;
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


    private class GetAddressTask extends AsyncTask<GeoPoint, Void, String> {
        private TextView addressTextView;

        public GetAddressTask(TextView addressTextView) {
            this.addressTextView = addressTextView;
        }

        @Override
        protected String doInBackground(GeoPoint... params) {
            GeoPoint geoPoint = params[0];
            String address = getAddressFromNominatim(geoPoint);
            return address;
        }

        @Override
        protected void onPostExecute(String address) {
            addressTextView.setText("Address: " + address);
        }
    }

    private String getAddressFromNominatim(GeoPoint geoPoint) {
        try {
            double latitude = geoPoint.getLatitude();
            double longitude = geoPoint.getLongitude();
            String urlString = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude + "&addressdetails=1";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            int read;
            char[] buff = new char[1024];
            StringBuilder json = new StringBuilder();

            while ((read = in.read(buff)) != -1) {
                json.append(buff, 0, read);
            }

            JSONObject jsonObject = new JSONObject(json.toString());
            if (jsonObject.has("display_name")) {
                return jsonObject.getString("display_name");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }

    private boolean markersExistForKeywords(String[] keywords) {
        for (String keyword : keywords) {
            String trimmedKeyword = keyword.trim();
            for (org.osmdroid.views.overlay.Overlay overlay : mapView.getOverlays()) {
                if (overlay instanceof Marker) {
                    Marker marker = (Marker) overlay;
                    String markerKeywords = marker.getTitle();

                    if (markerKeywords != null && markerKeywords.toLowerCase().contains(trimmedKeyword.toLowerCase())) {
                        // If a marker exists for this keyword, return true
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
