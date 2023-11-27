package com.example.geodes_mobile.main_app;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.fragments.AlertsFragment;
import com.example.geodes_mobile.fragments.FeedbackFragment;
import com.example.geodes_mobile.fragments.HelpFragment;
import com.example.geodes_mobile.fragments.OfflineMapFragment;
import com.example.geodes_mobile.fragments.ScheduleFragment;
import com.example.geodes_mobile.fragments.SettingsFragment;
import com.example.geodes_mobile.fragments.UserProfile_Fragment;
import com.example.geodes_mobile.main_app.bottom_sheet_content.addingSchedule.add_sched_dialog;
import com.example.geodes_mobile.main_app.bottom_sheet_content.adding_alerts_sched.Adapter5;
import com.example.geodes_mobile.main_app.bottom_sheet_content.adding_alerts_sched.DataModel5;
import com.example.geodes_mobile.main_app.bottom_sheet_content.alerts_section.Adapter;
import com.example.geodes_mobile.main_app.bottom_sheet_content.alerts_section.DataModel;
import com.example.geodes_mobile.main_app.bottom_sheet_content.schedules_section.Adapter2;
import com.example.geodes_mobile.main_app.bottom_sheet_content.schedules_section.DataModel2;
import com.example.geodes_mobile.main_app.create_geofence_functions.GeofenceBroadcastReceiver;
import com.example.geodes_mobile.main_app.create_geofence_functions.GeofenceHelper;
import com.example.geodes_mobile.main_app.create_geofence_functions.GeofenceSetup;
import com.example.geodes_mobile.main_app.create_geofence_functions.MapFunctionHandler;
import com.example.geodes_mobile.main_app.homebtn_functions.AlertEditDialog;
import com.example.geodes_mobile.main_app.homebtn_functions.LandmarksDialog;
import com.example.geodes_mobile.main_app.homebtn_functions.SchedEditDialog;
import com.example.geodes_mobile.main_app.homebtn_functions.TilesLayout;
import com.example.geodes_mobile.main_app.search_location.LocationResultt;
import com.example.geodes_mobile.main_app.search_location.SearchResultsAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class map_home extends AppCompatActivity {
    MapView mapView;
    private boolean isFirstButtonColor1 = true;
    private boolean isSecondButtonColor1 = true;
    private boolean isThirdButtonColor1 = true;
    private Button maptile;
    private Button landmarks;
    private Button userloc;
    private Button add_geofence;
    private Button cancelbtn;
    private Button btnDiscard;
    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintLayout changePosLayout;
    private NavigationView navigationView;
    private GeofenceSetup setup;

    private static final double MIN_ZOOM_LEVEL = 4.0;
    private static final double MAX_ZOOM_LEVEL = 21.0;
    private SeekBar outerSeekBar;
    private SeekBar innerSeekBar;
    private MapFunctionHandler locationHandler;
    private SearchView searchView;

    private Button dicardAddSched;

    private ImageButton closeAlerts;
    private ImageButton closeSched;

    private RecyclerView recyclerViewSearchResults;

    private Button addRadius;
    private Context context = this;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private Button addGeo;

    private View weatherview;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private TextView outerLabel;
    private TextView innerLabel;

    private Polygon outerGeofence;
    private Polygon innerGeofence;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    static boolean isButtonClicked = false;

    private List<String> inner;
    private List<String> outer;

    private EditText alertName;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private static final String PREFS_NAME = "GeofencePrefs";
    private static final String KEY_UNIQUE_ID = "uniqueID";
    private static final String KEY_GEO_NAME = "geoName";
    private static final String KEY_EMAIL = "email";
    private EditText alertBoxName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_maphome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("geofences");


        mapView = findViewById(R.id.map);
        mapView.setBuiltInZoomControls(false);

        SharedPreferences sharedPreferences = getSharedPreferences("radio_state", MODE_PRIVATE);
        int selectedRadioButtonId = sharedPreferences.getInt("selected_radio_id", R.id.Standard);

        // Set the map tile source based on the saved radio button ID
        if (selectedRadioButtonId == R.id.Standard) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
        } else if (selectedRadioButtonId == R.id.cycle) {
            mapView.setTileSource(TileSourceFactory.ChartbundleENRH);
        } else if (selectedRadioButtonId == R.id.USGS) {
            mapView.setTileSource(TileSourceFactory.USGS_SAT);
        } else if (selectedRadioButtonId == R.id.open_topo) {
            mapView.setTileSource(TileSourceFactory.OpenTopo);
        }
        mapView.setMultiTouchControls(true);

        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(mapView);
        rotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(rotationGestureOverlay);

        mapView.getController().setCenter(new GeoPoint(13.41, 122.56));
        mapView.getController().setZoom(8.0);
        mapView.setMinZoomLevel(MIN_ZOOM_LEVEL);
        mapView.setMaxZoomLevel(MAX_ZOOM_LEVEL);


        // Check and request location permissions if needed


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If not granted, request ACCESS_FINE_LOCATION permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "In order for the app to work properly please enable 'Allow all the time'", Toast.LENGTH_SHORT).show();
                // If not granted, request ACCESS_BACKGROUND_LOCATION permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 456);
            }


            // Set up location request
            locationRequest = new LocationRequest();
            locationRequest.setInterval(10000); // 10 seconds
            locationRequest.setFastestInterval(5000); // 5 seconds
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Set up location callback
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        GeoPoint userLocation = new GeoPoint(latitude, longitude);

                        // Display coordinates in a Toast
                        //String coordinatesMessage = "Latitude: " + latitude + "\nLongitude: " + longitude;
                        //Toast.makeText(getApplicationContext(), coordinatesMessage, Toast.LENGTH_SHORT).show();

                        // Do something else with the location if needed (e.g., update UI or send to a server)
                    }
                }
            };
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        // Check location settings
        checkLocationSettings();



        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper();



        clearGeo("3590","1963");

        maptile = findViewById(R.id.maptiles);
        landmarks = findViewById(R.id.landmarks);
        userloc = findViewById(R.id.colorChangingButton3);
        add_geofence = findViewById(R.id.colorChangingButton4);
        cancelbtn = findViewById(R.id.cancelButton);
        btnDiscard = findViewById(R.id.btnDiscard);
        dicardAddSched = findViewById(R.id.discardSched);
        closeAlerts = findViewById(R.id.CloseAlert);
        closeSched = findViewById(R.id.closeSchedule);
        addRadius = findViewById(R.id.btnSave1);
        searchView = findViewById(R.id.search_func);
        addGeo = findViewById(R.id.addGeofenceButton);
        weatherview = findViewById(R.id.WeatherView);

        //seekbars for geofence resizing
        outerSeekBar = findViewById(R.id.outerBar);
        innerSeekBar = findViewById(R.id.innerBar);

        //Label for seekbar progress geofence
        outerLabel = findViewById(R.id.OuterFenceValue);
        innerLabel = findViewById(R.id.InnerFenceValue);
        alertName = findViewById(R.id.AlertBoxname);



        //coordinatestext
        TextView coordinatesVal = findViewById(R.id.coordinatesValue1);



        // Set the rounded button background with initial colors
        setRoundedButtonBackground(maptile, R.color.white, R.color.green);
        setRoundedButtonBackground(landmarks, R.color.white, R.color.green);
        setRoundedButtonBackground(userloc, R.color.white, R.color.green);
        setRoundedButtonBackground(add_geofence, R.color.white, R.color.green);


        locationHandler = new MapFunctionHandler(map_home.this, mapView, coordinatesVal, outerSeekBar, innerSeekBar, outerLabel, innerLabel);


        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);


        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewSearchResults.setLayoutManager(layoutManager);

        // Set up the adapter for search results
        SearchResultsAdapter adapter = new SearchResultsAdapter();
        recyclerViewSearchResults.setAdapter(adapter);




        Button buttonStart = findViewById(R.id.btnStart);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the flag to true when the button is clicked

                showElements();

                GeoPoint retrievedGeoPoint = MapFunctionHandler.getMarkerLocation();
                float outer = (float) MapFunctionHandler.getOuterRadius();
                float inner = (float) MapFunctionHandler.getInnerRadius();

                setup = new GeofenceSetup(context, mapView);

                locationHandler.centerBoundingBox(retrievedGeoPoint, outer);

                isButtonClicked = true;

                if (isButtonClicked) {
                    locationHandler.dropPinOnMap1();
                }

                BoundingBox boundingBox = locationHandler.centerBoundingBox(retrievedGeoPoint, outer);

                mapView.zoomToBoundingBox(boundingBox,true);

                // Reset map orientation
                mapView.setMapOrientation(0);

                mapView.invalidate();


                String enteredText = alertName.getText().toString();

                // It's not clear what createGeofences method does, make sure it's using the correct geofenceSetup instance

                if (locationHandler.geTEntryOrExit()) {
                    String outerCode = geofenceHelper.OuterVal();
                    String innerCode = geofenceHelper.innerVal();
                    createGeofences(retrievedGeoPoint, enteredText, outer, inner, outerCode, innerCode);
                } else if (!locationHandler.geTEntryOrExit()) {
                    String ExitCode = geofenceHelper.generateRequestId();
                    createExitGeofence(retrievedGeoPoint, enteredText, outer, ExitCode);
                }

                // Call clearGeofencesAndMarker on the existing geofenceSetup instance
                Toast.makeText(context, retrievedGeoPoint + "  " + outer + "  " + inner, Toast.LENGTH_SHORT).show();
            }
        });




        Button buttonSave = findViewById(R.id.btnSave2);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    saveGeofenceDataToFirestore(currentUser);
                } else {
                    saveGeofenceDataToLocal();
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // Check if the query is in the format of coordinates (e.g., "12.345,67.890")
                if (isValidCoordinates(query)) {
                    try {
                        // Handle as coordinates
                        String[] parts = query.split(",");
                        double latitude = Double.parseDouble(parts[0]);
                        double longitude = Double.parseDouble(parts[1]);

                        GeoPoint point = new GeoPoint(latitude, longitude);
                        locationHandler.dropPinOnMap(point);
                        findViewById(R.id.noRes).setVisibility(View.GONE);
                        findViewById(R.id.search_res_view).setVisibility(View.GONE);

                    } catch (NumberFormatException e) {
                        // Display a toast message for invalid coordinates
                        Toast.makeText(getApplicationContext(), "Invalid coordinate format", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // Perform search as the user types
                if (newText.length() > 2) {
                    new GooglePlacesTask().execute(newText);
                    findViewById(R.id.search_res_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.noRes).setVisibility(View.GONE); // Hide "no results" message
                } else {
                    adapter.clear();
                    findViewById(R.id.search_res_view).setVisibility(View.GONE);
                    findViewById(R.id.noRes).setVisibility(View.GONE); // Hide "no results" message

                }
                return true;
            }
        });



        maptile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TilesLayout tilesLayout = new TilesLayout(map_home.this, mapView);
                tilesLayout.show();
                mapView.invalidate();
            }
        });


        Button openDialogButton = findViewById(R.id.landmarks);
        openDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a fixed Location object for Mall of Asia
                Location currentLocation = getCurrentLocation();

                LandmarksDialog landmarksDialog = new LandmarksDialog(map_home.this, mapView, currentLocation, locationHandler);
                landmarksDialog.show();
            }
        });


        ImageButton openAddAlertButton = findViewById(R.id.addAlerts);
        openAddAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_sched_dialog alertsAddDialog = new add_sched_dialog(map_home.this);
                alertsAddDialog.show();
            }
        });


        userloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locateUser();

                FirebaseUser currentUser = mAuth.getCurrentUser();
                String uid = currentUser.getUid();
                String email = currentUser.getEmail();

                Toast.makeText(map_home.this, uid + " " + email, Toast.LENGTH_SHORT).show();


            }
        });


        RelativeLayout overlayLayout = findViewById(R.id.overlayLayout);
        add_geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLongPressEnabled(false);
                findViewById(R.id.menu_button).setVisibility(View.GONE);
                findViewById(R.id.search_card).setVisibility(View.GONE);
                findViewById(R.id.frame_layout).setVisibility(View.GONE);
                // Show the overlay
                overlayLayout.setVisibility(View.VISIBLE);

            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.menu_button).setVisibility(View.VISIBLE);
                findViewById(R.id.search_card).setVisibility(View.VISIBLE);
                findViewById(R.id.frame_layout).setVisibility(View.VISIBLE);
                overlayLayout.setVisibility(View.GONE);
                setLongPressEnabled(true);

            }
        });

        //discard creating geofence
        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showElements();
                locationHandler.dropPinOnMap1();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // Reset the state
            }
        });

        //add geofence button from crosshair image

        addGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GeoPoint centerPoint = (GeoPoint) mapView.getMapCenter();
                locationHandler.dropPinOnMap(centerPoint);
                setLongPressEnabled(true);
                onBackPressed();
                findViewById(R.id.search_card).setVisibility(View.GONE);
            }
        });


        //add geofence button from BottomsheetRadii

        addRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //time picker in addsched bottom sheet
        TextView txtTimePicker = findViewById(R.id.txtTimePicker);
        txtTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        map_home.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker view, int selectedHour, int selectedMinute) {
                                // Convert 24-hour format to 12-hour format
                                int hourOfDay = selectedHour % 12;
                                String amPm = (selectedHour >= 12) ? "PM" : "AM";

                                // Handle the selected time, e.g., update the TextView
                                txtTimePicker.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, selectedMinute, amPm));
                            }
                        },
                        hour,
                        minute,
                        false // Set to false to use 12-hour format
                );

                timePickerDialog.show();
            }
        });



        //discrd adding schdule (bottom sheet)
        dicardAddSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.add_schedule).setVisibility(View.GONE);

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

                getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                setLongPressEnabled(true);
                showElements();
            }
        });

        //close alerts ng view alerts layout (bottomsheet)
        closeAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.viewAlert).setVisibility(View.GONE);
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                setLongPressEnabled(true);
                showElements();
            }
        });

        //close alerts ng view schedule layout(bottom sheet)
        closeSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.viewSchedule).setVisibility(View.GONE);

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

                getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                setLongPressEnabled(true);
                showElements();
            }
        });


        //edit the view alert
        ImageButton editalert = findViewById(R.id.EditAlertIcon);
        editalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log statement indicating that the ImageButton was tapped
                Log.d("ImageButton", "EditAlertIcon tapped");

                AlertEditDialog alertEditDialog = new AlertEditDialog(map_home.this);
                alertEditDialog.show();
            }
        });


        //edit the viewsched
        ImageButton editsched = findViewById(R.id.EditSchedule);
        editsched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log statement indicating that the ImageButton was tapped
                Log.d("ImageButton", "EditAlertIcon tapped");

                SchedEditDialog schedEditDialog = new SchedEditDialog(map_home.this);
                schedEditDialog.show();
            }
        });





        // Bottom Sheet
        LinearLayout linearLayout = findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        int customHeight = getResources().getDimensionPixelSize(R.dimen.custom_height);

        bottomSheetBehavior.setPeekHeight(customHeight);
        changePosLayout = findViewById(R.id.changePos);

        linearLayout.setTranslationY(customHeight);

        linearLayout.animate()
                .translationY(0)  // Animate to the original position
                .setDuration(500)  // Set the duration of the animation in milliseconds
                .start();

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Handle state changes if needed
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Update the vertical position of the ConstraintLayout with buttons
                int layoutHeight = changePosLayout.getHeight();
                int offset = (int) ((slideOffset * 0.90 * layoutHeight));
                changePosLayout.setTranslationY(-offset);
            }
        });


        //dito maglalagay ng ng list of active alerts
        List<DataModel> dataForAlerts = new ArrayList<>();
        dataForAlerts.add(new DataModel("54 km", R.drawable.get_in, "Alert#1", "no noteeddwdwdwdwdwdwdwswdiiuiuiuiuiuiuisadsdsadsade", R.drawable.pinalerts));
        dataForAlerts.add(new DataModel("5246 km", R.drawable.get_in, "Alert#2", "no noote", R.drawable.pinalerts));
        dataForAlerts.add(new DataModel("3 km", R.drawable.get_in, "Alert#3", "no notte", R.drawable.pinalerts));
        dataForAlerts.add(new DataModel("9 km", R.drawable.get_in, "Alert#7", "no not8e", R.drawable.pinalerts));
        dataForAlerts.add(new DataModel("54 km", R.drawable.get_in, "Alert#1", "no notee", R.drawable.pinalerts));
        dataForAlerts.add(new DataModel("9 km", R.drawable.get_in, "Alert#7", "no not8e", R.drawable.pinalerts));


        RecyclerView recyclerView = findViewById(R.id.alerts_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Adapter adapter4 = new Adapter(dataForAlerts, this);
        recyclerView.setAdapter(adapter4);

        //dito maglalagay ng list of active schedules
        List<DataModel2> dataForSched = new ArrayList<>();
        dataForSched.add(new DataModel2("Work", R.drawable.schedule_ic, R.drawable.calendar_ic, "10:00 AM", "Every day", R.drawable.alarm_ic, "Alert List 1"));
        dataForSched.add(new DataModel2("Work", R.drawable.schedule_ic, R.drawable.calendar_ic, "10:00 AM", "Every day", R.drawable.alarm_ic, "Alert List 1"));
        dataForSched.add(new DataModel2("Work", R.drawable.schedule_ic, R.drawable.calendar_ic, "10:00 AM", "Every day", R.drawable.alarm_ic, "Alert List 1"));
        dataForSched.add(new DataModel2("Work", R.drawable.schedule_ic, R.drawable.calendar_ic, "10:00 AM", "Every day", R.drawable.alarm_ic, "Alert List 1"));

        RecyclerView recyclerVieww = findViewById(R.id.sched_recyclerView);
        recyclerVieww.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Adapter2 adapter1 = new Adapter2(dataForSched, this);
        recyclerVieww.setAdapter(adapter1);



        //dito yung list of selected alerts sa schedules
        List<DataModel5> dataList = new ArrayList<>();

        dataList.add(new DataModel5("Alert 1", R.drawable.get_in));
        dataList.add(new DataModel5("Alert 1", R.drawable.get_in));
        dataList.add(new DataModel5("Alert 1", R.drawable.get_in));
        dataList.add(new DataModel5("Alert 1", R.drawable.get_in));
        dataList.add(new DataModel5("Alert 1", R.drawable.get_in));

        RecyclerView recyclerViewSched = findViewById(R.id.scheds_recyclerView);
        recyclerViewSched.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Adapter5 adapterSched = new Adapter5(dataList, this);
        recyclerViewSched.setAdapter(adapterSched);





        //Menu Drawer
        ImageButton menuButton = findViewById(R.id.menu_button);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        navigationView = findViewById(R.id.nav_view);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.alerts) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new AlertsFragment())
                            .commit();
                } else if (item.getItemId() == R.id.schedules) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new ScheduleFragment())
                            .commit();
                } else if (item.getItemId() == R.id.offline) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new OfflineMapFragment())
                            .commit();
                } else if (item.getItemId() == R.id.settings) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new SettingsFragment())
                            .commit();
                } else if (item.getItemId() == R.id.help) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new HelpFragment())
                            .commit();
                } else if (item.getItemId() == R.id.feedback) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new FeedbackFragment())
                            .commit();
                } else if (item.getItemId() == R.id.userprof) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new UserProfile_Fragment())
                            .commit();
                } else if (item.getItemId() == R.id.logout) {

                    mAuth.signOut();
                    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user == null) {
                                // The user is successfully signed out
                                Intent intent = new Intent(map_home.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                                // Additional actions after logging out if needed
                                Toast.makeText(map_home.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                                // Remove the listener to avoid unnecessary callbacks
                                mAuth.removeAuthStateListener(this);
                            }
                        }
                    });


                }

                drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after an item is selected
                return true;
            }
        });
    }




    private void setRoundedButtonBackground(Button button, int backgroundColor, int textColor) {
        GradientDrawable roundedDrawable = new GradientDrawable();
        roundedDrawable.setShape(GradientDrawable.RECTANGLE);
        roundedDrawable.setCornerRadius(100); // Adjust the radius as needed
        roundedDrawable.setColor(ContextCompat.getColor(this, backgroundColor));

        button.setBackground(roundedDrawable);
        button.setTextColor(ContextCompat.getColor(this, textColor));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            RelativeLayout overlayLayout = findViewById(R.id.overlayLayout);
            LinearLayout overlayLayoutt = findViewById(R.id.add_geo_btm);
            if (overlayLayout.getVisibility() == View.VISIBLE) {
                findViewById(R.id.menu_button).setVisibility(View.VISIBLE);
                findViewById(R.id.search_card).setVisibility(View.VISIBLE);
                findViewById(R.id.frame_layout).setVisibility(View.VISIBLE);
                overlayLayout.setVisibility(View.GONE);
            } else if (overlayLayoutt.getVisibility() == View.VISIBLE) {
                showElements();
                overlayLayoutt.setVisibility(View.GONE);
                locationHandler.dropPinOnMap1();
            } else {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                if (currentFragment instanceof AlertsFragment ||
                        currentFragment instanceof ScheduleFragment ||
                        currentFragment instanceof SettingsFragment ||
                        currentFragment instanceof OfflineMapFragment ||
                        currentFragment instanceof FeedbackFragment ||
                        currentFragment instanceof HelpFragment ||
                        currentFragment instanceof UserProfile_Fragment) {

                    // If the ScheduleFragment is hidden, show it
                    if (currentFragment instanceof ScheduleFragment && currentFragment.isHidden()
                            || currentFragment instanceof AlertsFragment && currentFragment.isHidden()) {
                        getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
                        locationHandler.setLongPressEnabled(true);
                        findViewById(R.id.add_schedule).setVisibility(View.GONE);
                        findViewById(R.id.viewSchedule).setVisibility(View.GONE);
                        findViewById(R.id.viewAlert).setVisibility(View.GONE);
                    } else {
                        // If it's another fragment, remove it
                        LinearLayout overlayLayouttt = findViewById(R.id.add_schedule);
                        overlayLayouttt.setVisibility(View.GONE);
                        showElements();
                        fragmentManager.beginTransaction().remove(currentFragment).commit();
                    }
                } else {
                    super.onBackPressed();
                }
            }
        }
    }


    private void locateUser() {
        MyLocationNewOverlay myLocationOverlay = (MyLocationNewOverlay) mapView.getOverlays().stream()
                .filter(overlay -> overlay instanceof MyLocationNewOverlay)
                .findFirst()
                .orElse(null);

        if (myLocationOverlay != null) {
            Location lastKnownLocation = myLocationOverlay.getLastFix();
            if (lastKnownLocation != null) {
                GeoPoint userLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mapView.getController().animateTo(userLocation);
            }
        }
    }


    public void BottomSheetRadii() {
        hideElements(false);

        final LinearLayout linearLayout = findViewById(R.id.add_geo_btm);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        int customHeight = getResources().getDimensionPixelSize(R.dimen.height_btm_add);
        bottomSheetBehavior.setPeekHeight(customHeight);

        changePosLayout = findViewById(R.id.changePos);

        linearLayout.setTranslationY(customHeight);

        // Animate the bottom sheet to slide up
        linearLayout.animate()
                .translationY(0)
                .setDuration(500)
                .start();

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Handle state changes if needed
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Update the vertical position of the ConstraintLayout with buttons

                int offset = (int) ((slideOffset));
                changePosLayout.setTranslationY(-offset);
            }
        });
    }




    public void BottomSheetAddSched() {
        LinearLayout overlayLayouttt = findViewById(R.id.add_schedule);
        overlayLayouttt.setVisibility(View.VISIBLE);

        locationHandler.setLongPressEnabled(false);
        LinearLayout linearLayout = findViewById(R.id.add_schedule);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        int customHeight = getResources().getDimensionPixelSize(R.dimen.height_btm_add);

        bottomSheetBehavior.setPeekHeight(customHeight);
        changePosLayout = findViewById(R.id.changePos);

        // Initially hide the bottom sheet off-screen
        linearLayout.setTranslationY(customHeight);

        // Animate the appearance of the bottom sheet
        linearLayout.animate()
                .translationY(0)  // Animate to the original position
                .setDuration(500)  // Set the duration of the animation in milliseconds
                .start();

        // Set the BottomSheet callback
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Handle state changes if needed
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Update the vertical position of the ConstraintLayout with buttons
                int layoutHeight = changePosLayout.getHeight();
                int offset = (int) ((slideOffset * 0.90 * layoutHeight));
                changePosLayout.setTranslationY(-offset);
            }
        });
    }


    public void ViewSched() {
        RelativeLayout overlayLayouttt = findViewById(R.id.viewSchedule);
        overlayLayouttt.setVisibility(View.VISIBLE);

        RelativeLayout linearLayout = findViewById(R.id.viewSchedule);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        int customHeight = getResources().getDimensionPixelSize(R.dimen.height_btm_add);

        bottomSheetBehavior.setPeekHeight(customHeight);
        changePosLayout = findViewById(R.id.changePos);

        // Initially hide the bottom sheet off-screen
        linearLayout.setTranslationY(customHeight);

        // Animate the appearance of the bottom sheet
        linearLayout.animate()
                .translationY(0)  // Animate to the original position
                .setDuration(500)  // Set the duration of the animation in milliseconds
                .start();
    }



    public void ViewAlerts() {
        RelativeLayout overlayLayouttt = findViewById(R.id.viewAlert);
        overlayLayouttt.setVisibility(View.VISIBLE);

        RelativeLayout linearLayout = findViewById(R.id.viewAlert);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        int customHeight = getResources().getDimensionPixelSize(R.dimen.height_btm_add);

        bottomSheetBehavior.setPeekHeight(customHeight);
        changePosLayout = findViewById(R.id.changePos);

        // Initially hide the bottom sheet off-screen
        linearLayout.setTranslationY(customHeight);

        // Animate the appearance of the bottom sheet
        linearLayout.animate()
                .translationY(0)  // Animate to the original position
                .setDuration(500)  // Set the duration of the animation in milliseconds
                .start();
    }


    public void hideElements(boolean hideOverlayLayoutt) {
        findViewById(R.id.menu_button).setVisibility(View.GONE);
        findViewById(R.id.search_card).setVisibility(View.GONE);
        findViewById(R.id.design_bottom_sheet).setVisibility(View.GONE);
        findViewById(R.id.maptiles).setVisibility(View.GONE);
        findViewById(R.id.landmarks).setVisibility(View.GONE);
        findViewById(R.id.colorChangingButton4).setVisibility(View.GONE);
        findViewById(R.id.colorChangingButton3).setVisibility(View.GONE);
        findViewById(R.id.search_card).setVisibility(View.GONE);


        LinearLayout overlayLayoutt = findViewById(R.id.add_geo_btm);
        overlayLayoutt.setVisibility(hideOverlayLayoutt ? View.GONE : View.VISIBLE);
    }


    public void showElements() {
        findViewById(R.id.menu_button).setVisibility(View.VISIBLE);
        findViewById(R.id.search_card).setVisibility(View.VISIBLE);
        findViewById(R.id.design_bottom_sheet).setVisibility(View.VISIBLE);
        findViewById(R.id.maptiles).setVisibility(View.VISIBLE);
        findViewById(R.id.landmarks).setVisibility(View.VISIBLE);
        findViewById(R.id.colorChangingButton4).setVisibility(View.VISIBLE);
        findViewById(R.id.colorChangingButton3).setVisibility(View.VISIBLE);
        findViewById(R.id.search_card).setVisibility(View.VISIBLE);


        LinearLayout overlayLayoutt = findViewById(R.id.add_geo_btm);
        overlayLayoutt.setVisibility(View.GONE);
    }




    private class GooglePlacesTask extends AsyncTask<String, Void, ArrayList<LocationResultt>> {
        @Override
        protected ArrayList<LocationResultt> doInBackground(String... params) {
            ArrayList<LocationResultt> results = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            // Replace "YOUR_GOOGLE_API_KEY" with your actual Google API key
            String apiKey = "AIzaSyA-PwG-IjCROFu9xXBRizCuyz8L83V8Guc";
            String url = "https://maps.googleapis.com/maps/api/place/textsearch/json" +
                    "?query=" + params[0] +
                    "&key=" + apiKey;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                // Check if the response contains results
                if (jsonObject.has("results")) {
                    JSONArray resultsArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject resultObject = resultsArray.getJSONObject(i);
                        JSONObject geometry = resultObject.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double latitude = location.optDouble("lat", 0);
                        double longitude = location.optDouble("lng", 0);
                        String name = resultObject.optString("name", "");
                        String address = resultObject.optString("formatted_address", "");

                        // Construct the display name combining name and address if needed
                        String displayName = name + ", " + address;

                        results.add(new LocationResultt(displayName, latitude, longitude));
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<LocationResultt> results) {
            super.onPostExecute(results);

            SearchResultsAdapter adapter = (SearchResultsAdapter) recyclerViewSearchResults.getAdapter();
            adapter.setData(results);

            if (results.isEmpty() && !searchView.getQuery().toString().isEmpty()) {
                // Show "no results" message if the results list is empty and the search query is not empty
                findViewById(R.id.noRes).setVisibility(View.VISIBLE);
                findViewById(R.id.search_res_view).setVisibility(View.GONE);
            } else {
                // Hide "no results" message if there are search results or the search query is empty
                findViewById(R.id.noRes).setVisibility(View.GONE);
            }

            // Set the click listener for the search results
            adapter.setOnItemClickListener(locationResult -> {
                // Handle the click action here, display coordinates as a toast
                GeoPoint point = new GeoPoint(locationResult.getLatitude(), locationResult.getLongitude());
                locationHandler.dropPinOnMap(point);

                findViewById(R.id.search_res_view).setVisibility(View.GONE);
                showToast(locationResult.getLatitude() + ", " + locationResult.getLongitude());
            });
        }

        private void showToast(String message) {
            // Display toast message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }




    private boolean isValidCoordinates(String input) {

        String[] parts = input.split(",");

        if (parts.length != 2) {
            return false;
        }


        try {
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            // You can add more specific validation rules here if needed

            if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude > 180.0) {
                return false; // Coordinates are out of valid range
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setLongPressEnabled(boolean enabled) {
        locationHandler.setLongPressEnabled(enabled);
    }




    private Location getCurrentLocation() {
        MyLocationNewOverlay myLocationOverlay = (MyLocationNewOverlay) mapView.getOverlays().stream()
                .filter(overlay -> overlay instanceof MyLocationNewOverlay)
                .findFirst()
                .orElse(null);

        if (myLocationOverlay != null && myLocationOverlay.getLastFix() != null) {
            Location geoPoint = myLocationOverlay.getLastFix();
            Location location = new Location("MyLocationOverlay");
            location.setLatitude(geoPoint.getLatitude());
            location.setLongitude(geoPoint.getLongitude());
            return location;
        }

        return null;
    }



    private void checkLocationSettings() {
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                task.getResult(ApiException.class);
                requestLocationUpdates();
            } catch (ApiException exception) {
                if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                        resolvable.startResolutionForResult(map_home.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException | ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            setupMyLocationOverlay();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void setupMyLocationOverlay() {
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location settings not satisfied, cannot proceed", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    private void createGeofences(GeoPoint markerPoint, String GeoName, float outerRadius, float innerRadius, String outerType, String innerType) {
        // Create outer geofence
        outerGeofence = new Polygon();
        outerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, outerRadius));
        outerGeofence.setFillColor(Color.argb(102, 154, 220, 241));
        outerGeofence.setStrokeColor(Color.rgb(80, 156, 180));
        outerGeofence.setStrokeWidth(3.0f);
        mapView.getOverlayManager().add(outerGeofence);

        // Create inner geofence
        innerGeofence = new Polygon();
        innerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, innerRadius));
        innerGeofence.setFillColor(Color.argb(50, 0, 255, 0));
        innerGeofence.setStrokeColor(Color.rgb(91, 206, 137));
        innerGeofence.setStrokeWidth(3.0f);
        mapView.getOverlayManager().add(innerGeofence);

        // Create marker
        Marker marker = new Marker(mapView);
        marker.setPosition(markerPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null);

        // Load a new custom Bitmap or image resource for the marker
        Bitmap customBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_loc);

        // Create a Drawable from the custom Bitmap
        Drawable customDrawable = new BitmapDrawable(getResources(), customBitmap);

        // Set the custom Drawable as the icon for the marker
        marker.setIcon(customDrawable);

        mapView.getOverlays().add(marker);
        mapView.invalidate();



        addGeofence(markerPoint, outerRadius, outerType, GeoName, true);
        addGeofence(markerPoint, innerRadius, innerType, GeoName, true);
        Log.d("GeofenceValues", "Outer Type: " + outerType);
        Log.d("GeofenceValues", "Inner Type: " + innerType);



    }


    private void createExitGeofence(GeoPoint markerPoint, String GeoName, float outerRadius, String ExitCode) {
        outerGeofence = new Polygon();
        outerGeofence.setPoints(Polygon.pointsAsCircle(markerPoint, outerRadius));
        outerGeofence.setFillColor(Color.argb(102, 241, 217, 154));
        outerGeofence.setStrokeColor(Color.argb(255, 180, 158, 80));
        outerGeofence.setStrokeWidth(3.0f);
        mapView.getOverlayManager().add(outerGeofence);

        Marker marker = new Marker(mapView);
        marker.setPosition(markerPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null);

        // Load a new custom Bitmap or image resource for the marker
        Bitmap customBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_loc_exit);

        // Create a Drawable from the custom Bitmap
        Drawable customDrawable = new BitmapDrawable(getResources(), customBitmap);

        // Set the custom Drawable as the icon for the marker
        marker.setIcon(customDrawable);

        mapView.getOverlays().add(marker);
        mapView.invalidate();


        addGeofence(markerPoint, outerRadius, ExitCode, GeoName,false);
        Log.d("GeofenceValues", "Inner Type: " + ExitCode);


    }


    private void addGeofence(GeoPoint latLng, float radius, String requestId, String geofenceName, boolean addEntryGeofence) {
        Geofence geofenceExit = geofenceHelper.createExitGeofence(latLng, radius, requestId);
        // Create GeofencingRequest for exit geofence
        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder()
                .addGeofence(geofenceExit);

        if (addEntryGeofence) {
            // If true, then entry geofence would be added
            Geofence geofenceEntry = geofenceHelper.createEntryGeofence(latLng, radius, requestId);
            geofencingRequestBuilder.addGeofence(geofenceEntry);
        }

        GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();

        PendingIntent pendingIntent = getGeofencePendingIntent(geofenceName);

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

    }


    private void clearGeo(String inner, String outer) {
        List<String> innerList = Collections.singletonList(inner);
        List<String> outerList = Collections.singletonList(outer);

        geofencingClient.removeGeofences(innerList)
                .addOnSuccessListener(aVoid -> {
                    Log.i("GeofenceRemoval", "Inner geofence removed successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("GeofenceRemoval", "Failed to remove inner geofence: " + e.getMessage());
                });

        geofencingClient.removeGeofences(outerList)
                .addOnSuccessListener(aVoid -> {
                    Log.i("GeofenceRemoval", "Outer geofence removed successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("GeofenceRemoval", "Failed to remove outer geofence: " + e.getMessage());
                });
    }


    private void removeGeofence(String geofenceName) {
        // Get the PendingIntent associated with the geofence
        PendingIntent geofencePendingIntent = getGeofencePendingIntent(geofenceName);

        // Remove the geofence using the geofencingClient
        geofencingClient.removeGeofences(geofencePendingIntent)
                .addOnSuccessListener(aVoid -> {
                    // Geofence removal was successful
                    Log.i("GeofenceRemoval", "Geofence removed successfully: " + geofenceName);
                })
                .addOnFailureListener(e -> {
                    // Geofence removal failed
                    Log.e("GeofenceRemoval", "Failed to remove geofence " + geofenceName + ": " + e.getMessage());
                });
    }




    private PendingIntent getGeofencePendingIntent(String geofenceName) {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        int flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        int requestCode = 0;
        // Pass geofenceName as an extra to the intent
        intent.putExtra("GEOFENCE_NAME", geofenceName);
        intent.setAction("com.example.geodes_mobile.main_app.create_geofence_functions.ACTION_GEOFENCE_TRANSITION");
        return PendingIntent.getBroadcast(this, requestCode, intent, flags);}



    /*private void saveGeofenceInFirestore(String uniqueId, String geoName) {
        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming you have a "geofences" collection in Firestore
        CollectionReference geofencesCollection = db.collection("geofences");

        // Create a document with the uniqueId as the document ID
        DocumentReference geofenceDocument = geofencesCollection.document(uniqueId);

        // Create a map to store geofence information
        Map<String, Object> geofenceData = new HashMap<>();
        geofenceData.put("uniqueId", uniqueId);
        geofenceData.put("geoName", geoName);

        // Set the data in the document
        geofenceDocument.set(geofenceData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Geofence data stored successfully");
                    // You can add any additional logic here after successful storage
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to store geofence data: " + e.getMessage());
                    // Handle the failure, if needed
                });
    }*/

    private void saveGeofenceDataToFirestore(FirebaseUser currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        alertBoxName = findViewById(R.id.AlertBoxname);
        String uid = currentUser.getUid();
        String geoName = alertBoxName.getText().toString(); // Replace with your geofence name

        // Create a unique ID for the geofence or use your own logic
        String geofenceId = geofenceHelper.generateRequestId();

        // Create a map to store geofence data
        Map<String, Object> geofenceData = new HashMap<>();
        geofenceData.put("uniqueID", geofenceId);
        geofenceData.put("alertName", geoName);
        geofenceData.put("email", currentUser.getEmail());

        // Add data to Firestore
        db.collection("geofences")
                .document(geofenceId)
                .set(geofenceData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data added successfully to Firestore
                        // You can add a Toast or other UI feedback here if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                    }
                });
    }
    private void saveGeofenceDataToLocal() {
        // Get or create SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Replace "defaultValue" with default values if needed
        String geofenceId = preferences.getString(KEY_UNIQUE_ID, "defaultValue");
        String geoName = preferences.getString(KEY_GEO_NAME, "defaultValue");
        String email = preferences.getString(KEY_EMAIL, "defaultValue");

        // Your existing geofence data
        String newGeofenceId = geofenceHelper.generateRequestId();
        String newGeoName = "YourGeofenceName"; // Replace with your geofence name

        // Save the new geofence data locally
        editor.putString(KEY_UNIQUE_ID, newGeofenceId);
        editor.putString(KEY_GEO_NAME, newGeoName);

        // Apply changes
        editor.apply();
    }



    public static void setButtonClicked(boolean clicked) {
        isButtonClicked = clicked;
    }

    public static boolean isButtonClicked() {
        return isButtonClicked;
    }

}