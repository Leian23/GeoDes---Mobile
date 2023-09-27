package com.example.geodes_mobile.main_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.geodes_mobile.R;
import com.example.geodes_mobile.fragments.AlertsFragment;
import com.example.geodes_mobile.fragments.FeedbackFragment;
import com.example.geodes_mobile.fragments.HelpFragment;
import com.example.geodes_mobile.fragments.OfflineMapFragment;
import com.example.geodes_mobile.fragments.ScheduleFragment;
import com.example.geodes_mobile.fragments.SettingsFragment;
import com.example.geodes_mobile.main_app.Landmarks_functions.LandmarksDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import org.osmdroid.views.overlay.Polyline;
import android.graphics.Paint;

import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;




public class map_home extends AppCompatActivity {
    //Map View Initialization
    private MapView mapView;
    private boolean isFirstButtonColor1 = true;
    private boolean isSecondButtonColor1 = true;
    private boolean isThirdButtonColor1 = true;

    private Button traffic;
    private Button landmarks;
    private Button userloc;
    private Button add_geofence;
    private Button cancelbtn;
    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintLayout changePosLayout;
    private NavigationView navigationView;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;
    private static final double MIN_ZOOM_LEVEL = 4.0; // Adjust the minimum zoom level as needed
    private static final double MAX_ZOOM_LEVEL = 21.0; // Adjust the maximum zoom level as needed



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_maphome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Enable pinch-to-zoom gestures on the map
        mapView.setMultiTouchControls(true);

        // Enable rotation gestures
        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(mapView);
        rotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(rotationGestureOverlay);

        mapView.getController().setCenter(new org.osmdroid.util.GeoPoint(13.41, 122.56));
        mapView.getController().setZoom(12.0);
        mapView.setMinZoomLevel(MIN_ZOOM_LEVEL);
        mapView.setMaxZoomLevel(MAX_ZOOM_LEVEL);


        // Check and request location permissions if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocationOverlay();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        traffic = findViewById(R.id.trafficbtn);
        landmarks = findViewById(R.id.landmarks);
        userloc = findViewById(R.id.colorChangingButton3);
        add_geofence = findViewById(R.id.colorChangingButton4);
        cancelbtn = findViewById(R.id.cancelButton);

        // Set the rounded button background with initial colors
        setRoundedButtonBackground(traffic, R.color.white, R.color.green);
        setRoundedButtonBackground(landmarks, R.color.white, R.color.green);
        setRoundedButtonBackground(userloc, R.color.white, R.color.green);
        setRoundedButtonBackground(add_geofence, R.color.white, R.color.green);





        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(traffic, isFirstButtonColor1);
                isFirstButtonColor1 = !isFirstButtonColor1;
            }
        });

        landmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor(landmarks, isSecondButtonColor1);
                isSecondButtonColor1 = !isSecondButtonColor1;
            }
        });

        Button openDialogButton = findViewById(R.id.landmarks);
        openDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LandmarksDialog landmarksDialog = new LandmarksDialog(map_home.this);
                landmarksDialog.show();
            }
        });


        userloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(map_home.this, "Button for locating users current loc", Toast.LENGTH_SHORT).show();
            }
        });



       RelativeLayout overlayLayout = findViewById(R.id.overlayLayout);
         add_geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.menu_button).setVisibility(View.GONE);
                findViewById(R.id.search_view1).setVisibility(View.GONE);
                findViewById(R.id.frame_layout).setVisibility(View.GONE);
                // Show the overlay
                overlayLayout.setVisibility(View.VISIBLE);


            }
        });




        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.menu_button).setVisibility(View.VISIBLE);
                findViewById(R.id.search_view1).setVisibility(View.VISIBLE);
                findViewById(R.id.frame_layout).setVisibility(View.VISIBLE);
                overlayLayout.setVisibility(View.GONE);



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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocationOverlay();
            } else {
                // Handle permission denied
                Toast.makeText(this, "Location permission denied. Cannot show your location on the map.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Initialize and enable the user's location overlay on the map
    private void enableMyLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Center the map on the user's location (if available)
        Location lastKnownLocation = myLocationOverlay.getLastFix();
        if (lastKnownLocation != null) {
            GeoPoint startPoint = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mapView.getController().animateTo(startPoint);
            mapView.getController().setZoom(15.0);
        }
    }

    // Method to add inner and outer circles with the user's location as the center
    private void addLocationCircles() {
        // Get the user's last known location
        Location lastKnownLocation = myLocationOverlay.getLastFix();
        if (lastKnownLocation != null) {
            double userLatitude = lastKnownLocation.getLatitude();
            double userLongitude = lastKnownLocation.getLongitude();

            // Create GeoPoint for the user's location
            GeoPoint userLocation = new GeoPoint(userLatitude, userLongitude);

            /// Create inner circle with a radius of 300 meters
            Polygon innerCircle = createCircle(userLocation, 300.0, 0x300000FF, 0x00000000, 0f); // Semi-transparent blue color with no outline

// Create outer circle with a radius of 1000 meters
            Polygon outerCircle = createCircle(userLocation, 1000.0, 0x3000FF00, 0x00000000, 0f); // Semi-transparent green color with no outline

            // Add circles to the map
            mapView.getOverlays().add(innerCircle);
            mapView.getOverlays().add(outerCircle);

            // Refresh the map to show the circles
            mapView.invalidate();
        }
    }


    // Method to create a circular polygon with an outline
    private Polygon createCircle(GeoPoint center, double radiusInMeters, int fillColor, int strokeColor, float strokeWidth) {
        int numberOfPoints = 360; // Number of points to approximate the circle

        // Create an ArrayList to hold the points of the circle
        ArrayList<GeoPoint> circlePoints = new ArrayList<>();

        double distanceX = radiusInMeters / 111320.0; // 1 degree of latitude is approximately 111320 meters
        double distanceY = radiusInMeters / (111320.0 * Math.cos(Math.toRadians(center.getLatitude())));

        for (int i = 0; i < numberOfPoints; i++) {
            double theta = Math.toRadians(i * 360.0 / numberOfPoints);
            double x = center.getLatitude() + (distanceX * Math.cos(theta));
            double y = center.getLongitude() + (distanceY * Math.sin(theta));
            circlePoints.add(new GeoPoint(x, y));
        }

        // Create the Polygon with the fill color
        Polygon circle = new Polygon(mapView);
        circle.setPoints(circlePoints);
        circle.getFillPaint().setColor(fillColor); // Set fill color

        // Create a Polyline for the outline
        Polyline polyline = new Polyline(mapView);
        polyline.setPoints(circlePoints);

        // Set stroke color and width using the outlinePaint
        Paint outlinePaint = polyline.getOutlinePaint();
        outlinePaint.setColor(strokeColor);
        outlinePaint.setStrokeWidth(strokeWidth);

        // Add the Polyline to the map overlays
        mapView.getOverlayManager().add(polyline);

        return circle;
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