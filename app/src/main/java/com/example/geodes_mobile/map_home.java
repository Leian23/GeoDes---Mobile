package com.example.geodes_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import android.graphics.Color;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import androidx.core.content.res.ResourcesCompat;


public class map_home extends AppCompatActivity {
    //Map View Initialization
    private MapView mapView;
    private boolean isFirstButtonColor1 = true;
    private boolean isSecondButtonColor1 = true;
    private boolean isThirdButtonColor1 = true;
    private boolean isFourthButtonColor1 = true;
    private Button firstButton;
    private Button secondButton;
    private Button thirdButton;
    private Button fourthButton;
    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintLayout changePosLayout;
    private NavigationView navigationView;
    private MyLocationNewOverlay myLocationOverlay;
    private List<CustomMarker> markers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_maphome);

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Enable zoom controls (plus and minus buttons) on the map
        mapView.setBuiltInZoomControls(true);

        // Enable pinch-to-zoom gestures on the map
        mapView.setMultiTouchControls(true);

        // Create a new MyLocationNewOverlay to show the user's location
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Request location permissions if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Create circles around the user's location
        createCircles(myLocationOverlay.getMyLocation());

        // Set the initial center to the user's location
        mapView.getController().setCenter(myLocationOverlay.getMyLocation());
        mapView.getController().setZoom(12.0);



        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                // Handle long press event here
                addMarkerToMap(p);
                return true; // Consume the long press event
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        mapView.getOverlays().add(mapEventsOverlay);

        firstButton = findViewById(R.id.colorChangingButton);
        secondButton = findViewById(R.id.colorChangingButton2);
        thirdButton = findViewById(R.id.colorChangingButton3);
        fourthButton = findViewById(R.id.colorChangingButton4);

        // Set the rounded button background with initial colors
        setRoundedButtonBackground(firstButton, R.color.white, R.color.green);
        setRoundedButtonBackground(secondButton, R.color.white, R.color.green);
        setRoundedButtonBackground(thirdButton, R.color.white, R.color.green);
        setRoundedButtonBackground(fourthButton, R.color.white, R.color.green);
        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(firstButton, isFirstButtonColor1);
                isFirstButtonColor1 = !isFirstButtonColor1;
            }
        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(secondButton, isSecondButtonColor1);
                isSecondButtonColor1 = !isSecondButtonColor1;


            }
        });

        thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(thirdButton, isThirdButtonColor1);
                isThirdButtonColor1 = !isThirdButtonColor1;
            }
        });

        fourthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(fourthButton, isFourthButtonColor1);
                isFourthButtonColor1 = !isFourthButtonColor1;
            }
        });



        //Bottom Sheet
        LinearLayout linearLayout = findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        int customHeight = getResources().getDimensionPixelSize(R.dimen.custom_height);

        bottomSheetBehavior.setPeekHeight(customHeight);


        changePosLayout = findViewById(R.id.changePos);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Handle state changes if needed
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Update the vertical position of the ConstraintLayout with buttons
                int layoutHeight = changePosLayout.getHeight();
                int offset = (int) ((slideOffset * 1.55 * layoutHeight));
                changePosLayout.setTranslationY(-offset);
            }
        });


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

        navigationView = findViewById(R.id.nav_view); // Make sure to initialize your NavigationView

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.alerts) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new AlertsFragment()) // Use your existing fragment instance or create a new one
                            .commit();
                } else if (item.getItemId() == R.id.schedules) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new ScheduleFragment()) // Use your existing fragment instance or create a new one
                            .commit();
                } else if (item.getItemId() == R.id.offline) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new OfflineMapFragment()) // Use your existing fragment instance or create a new one
                            .commit();
                } else if (item.getItemId() == R.id.settings) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new SettingsFragment()) // Use your existing fragment instance or create a new one
                            .commit();
                } else if (item.getItemId() == R.id.help) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new HelpFragment()) // Use your existing fragment instance or create a new one
                            .commit();
                } else if (item.getItemId() == R.id.feedback) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new FeedbackFragment()) // Use your existing fragment instance or create a new one
                            .commit();
                } else if (item.getItemId() == R.id.logout) {
                    Toast.makeText(map_home.this, "You have selected alerts", Toast.LENGTH_SHORT).show();
                }
                // Add more else-if blocks for other menu items if needed

                drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after an item is selected
                return true;

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable location updates
                myLocationOverlay.enableMyLocation();
                mapView.getController().setCenter(myLocationOverlay.getMyLocation());
            }
        }
    }

    private void createCircles(GeoPoint userLocation) {
        // Define circle parameters
        double innerRadius = 0.003; // 300 meters in degrees (adjust as needed)
        double outerRadius = 0.01;  // 1000 meters in degrees (adjust as needed)

        // Create circles using Polygon
        List<GeoPoint> innerCircle = createCircle(userLocation, innerRadius);
        List<GeoPoint> outerCircle = createCircle(userLocation, outerRadius);

        // Create and add circles to the map with updated fill color
        Polygon polygon1 = new Polygon();
        Polygon polygon2 = new Polygon();

        polygon1.setPoints(innerCircle);
        polygon2.setPoints(outerCircle);

        // Set the fill colors for the circles using getOutlinePaint()
        polygon1.getOutlinePaint().setColor(Color.argb(50, 0, 0, 255)); // Blue
        polygon2.getOutlinePaint().setColor(Color.argb(50, 255, 0, 0)); // Red

        mapView.getOverlayManager().add(polygon1);
        mapView.getOverlayManager().add(polygon2);

        mapView.invalidate(); // Refresh the map to display the circles
    }

    private List<GeoPoint> createCircle(GeoPoint center, double radius) {
        List<GeoPoint> circlePoints = new ArrayList<>();
        int numPoints = 100; // Number of points to create a circle

        for (int i = 0; i < numPoints; i++) {
            double angle = Math.toRadians((360.0 / numPoints) * i);
            double latitude = center.getLatitude() + (radius / 111.32) * Math.cos(angle); // 111.32 km is the approximate distance between one degree of latitude
            double longitude = center.getLongitude() + (radius / (111.32 * Math.cos(Math.toRadians(center.getLatitude())))) * Math.sin(angle);
            circlePoints.add(new GeoPoint(latitude, longitude));
        }

        return circlePoints;
    }

    private void addMarkerToMap(GeoPoint geoPoint) {
        // Create a marker and add it to the map
        Marker marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.traffic_ic, null)); // Set your custom marker icon
        mapView.getOverlays().add(marker);

        // Create a custom marker and add it to the list
        CustomMarker customMarker = new CustomMarker(geoPoint, "Custom Marker");
        markers.add(customMarker);

        // Refresh the map to display the marker
        mapView.invalidate();
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

    private void toggleButtonColor(Button button, boolean isColor1) {
        if (isColor1) {
            setRoundedButtonBackground(button, R.color.green, R.color.white);
        } else {
            setRoundedButtonBackground(button, R.color.white, R.color.green);
        }
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
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

            if (currentFragment instanceof AlertsFragment ||
                    currentFragment instanceof ScheduleFragment ||
                    currentFragment instanceof SettingsFragment ||
                    currentFragment instanceof OfflineMapFragment ||
                    currentFragment instanceof FeedbackFragment ||
                    currentFragment instanceof HelpFragment) {

                // You are in one of the specified fragments, navigate back to map_home
                Intent intent = new Intent(this, map_home.class);
                startActivity(intent);
                finish();
            } else {
                // Handle the back press as usual
                super.onBackPressed();
            }
        }
    }



}