package com.example.geodes_mobile.main_app;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.geodes_mobile.main_app.bottom_sheet_content.alerts_section.Adapter;
import com.example.geodes_mobile.main_app.bottom_sheet_content.alerts_section.DataModel;
import com.example.geodes_mobile.main_app.bottom_sheet_content.schedules_section.Adapter2;
import com.example.geodes_mobile.main_app.bottom_sheet_content.schedules_section.DataModel2;
import com.example.geodes_mobile.main_app.create_geofence_functions.MapFunctionHandler;
import com.example.geodes_mobile.main_app.homebtn_functions.LandmarksDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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
    private Button btnDiscard;
    private Button viewProfile;
    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintLayout changePosLayout;
    private NavigationView navigationView;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;
    private static final double MIN_ZOOM_LEVEL = 4.0;
    private static final double MAX_ZOOM_LEVEL = 21.0;

    private SeekBar outerSeekBar;
    private SeekBar innerSeekBar;
    private MapFunctionHandler locationHandler;
    private SearchView searchView;

    private RecyclerView recyclerViewSearchResults;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_maphome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);


        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(mapView);
        rotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(rotationGestureOverlay);


        mapView.getController().setCenter(new GeoPoint(13.41, 122.56));
        mapView.getController().setZoom(8.0);
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
        btnDiscard = findViewById(R.id.btnDiscard);
        outerSeekBar = findViewById(R.id.levelSeekBar);
        innerSeekBar = findViewById(R.id.levelSeekBar2);


        // Set the rounded button background with initial colors
        setRoundedButtonBackground(traffic, R.color.white, R.color.green);
        setRoundedButtonBackground(landmarks, R.color.white, R.color.green);
        setRoundedButtonBackground(userloc, R.color.white, R.color.green);
        setRoundedButtonBackground(add_geofence, R.color.white, R.color.green);


        locationHandler = new MapFunctionHandler(map_home.this, mapView, outerSeekBar, innerSeekBar);



        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        searchView = findViewById(R.id.search_func);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewSearchResults.setLayoutManager(layoutManager);

        // Set up the adapter for search results
        SearchResultsAdapter adapter = new SearchResultsAdapter();
        recyclerViewSearchResults.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submission if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the user types
                if (newText.length() > 2) {
                    new NominatimTask().execute(newText);
                    findViewById(R.id.search_res_view).setVisibility(View.VISIBLE);
                } else {
                    adapter.clear();
                    findViewById(R.id.search_res_view).setVisibility(View.GONE);
                }
                return true;
            }
        });



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
                locateUser();
            }
        });


        RelativeLayout overlayLayout = findViewById(R.id.overlayLayout);
        add_geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showElements();
                locationHandler.clearMarkerAndGeofences();
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
                int offset = (int) ((slideOffset * 0.90 * layoutHeight));
                changePosLayout.setTranslationY(-offset);
            }
        });

        //dito maglalagay ng ng list of active alerts
        List<DataModel> data = new ArrayList<>();
        data.add(new DataModel("54 km", R.drawable.get_in, "Alert#1", "no noteeddwdwdwdwdwdwdwswdiiuiuiuiuiuiuisadsdsadsade", R.drawable.pinalerts));
        data.add(new DataModel("5246 km", R.drawable.get_in, "Alert#2", "no noote", R.drawable.pinalerts));
        data.add(new DataModel("3 km", R.drawable.get_in, "Alert#3", "no notte", R.drawable.pinalerts));
        data.add(new DataModel("9 km", R.drawable.get_in, "Alert#7", "no not8e", R.drawable.pinalerts));
        data.add(new DataModel("54 km", R.drawable.get_in, "Alert#1", "no notee", R.drawable.pinalerts));
        data.add(new DataModel("52 km", R.drawable.get_in, "Alert#2", "no noote", R.drawable.pinalerts));
        data.add(new DataModel("3 km", R.drawable.get_in, "Alert#3", "no notte", R.drawable.pinalerts));
        data.add(new DataModel("9 km", R.drawable.get_in, "Alert#7", "no not8e", R.drawable.pinalerts));
        data.add(new DataModel("54 km", R.drawable.get_in, "Alert#1", "no notee", R.drawable.pinalerts));
        data.add(new DataModel("52 km", R.drawable.get_in, "Alert#2", "no noote", R.drawable.pinalerts));
        data.add(new DataModel("3 km", R.drawable.get_in, "Alert#3", "no notte", R.drawable.pinalerts));
        data.add(new DataModel("9 km", R.drawable.get_in, "Alert#7", "no not8e", R.drawable.pinalerts));


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Adapter adapter4 = new Adapter(data, this);
        recyclerView.setAdapter(adapter4);

        //dito maglalagay ng list of active schedules
        List<DataModel2> data1 = new ArrayList<>();
        data1.add(new DataModel2("Work", R.drawable.schedule_ic, R.drawable.calendar_ic, "10:00 AM", "Every day", R.drawable.alarm_ic, "Alert List 1"));
        data1.add(new DataModel2("Work", R.drawable.schedule_ic, R.drawable.calendar_ic, "10:00 AM", "Every day", R.drawable.alarm_ic, "Alert List 1"));
        data1.add(new DataModel2("Work", R.drawable.schedule_ic, R.drawable.calendar_ic, "10:00 AM", "Every day", R.drawable.alarm_ic, "Alert List 1"));
        data1.add(new DataModel2("Work", R.drawable.schedule_ic, R.drawable.calendar_ic, "10:00 AM", "Every day", R.drawable.alarm_ic, "Alert List 1"));

        RecyclerView recyclerVieww = findViewById(R.id.recyclerVieww);
        recyclerVieww.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Adapter2 adapter1 = new Adapter2(data1, this);
        recyclerVieww.setAdapter(adapter1);




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
                }else if (item.getItemId() == R.id.logout) {
                    Toast.makeText(map_home.this, "You have selected alerts", Toast.LENGTH_SHORT).show();
                }

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
                locationHandler.clearMarkerAndGeofences();
            } else {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

                if (currentFragment instanceof AlertsFragment ||
                        currentFragment instanceof ScheduleFragment ||
                        currentFragment instanceof SettingsFragment ||
                        currentFragment instanceof OfflineMapFragment ||
                        currentFragment instanceof FeedbackFragment ||
                        currentFragment instanceof HelpFragment ||
                        currentFragment instanceof UserProfile_Fragment) {

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(currentFragment).commit();

                } else {
                    super.onBackPressed();
                }
            }
        }
    }


    private void locateUser() {
        if (myLocationOverlay != null) {
            Location lastKnownLocation = myLocationOverlay.getLastFix();
            if (lastKnownLocation != null) {
                GeoPoint userLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mapView.getController().animateTo(userLocation);
                mapView.getController().setZoom(15.0);
            }
        }
    }
    public void hideElements() {
        //hide all the elements when adding geofence (this is where ui for addding geofence is implemented)
        LinearLayout overlayLayoutt = findViewById(R.id.add_geo_btm);
        findViewById(R.id.menu_button).setVisibility(View.GONE);
        findViewById(R.id.search_card).setVisibility(View.GONE);
        findViewById(R.id.design_bottom_sheet).setVisibility(View.GONE);
        findViewById(R.id.trafficbtn).setVisibility(View.GONE);
        findViewById(R.id.landmarks).setVisibility(View.GONE);
        findViewById(R.id.colorChangingButton4).setVisibility(View.GONE);
        findViewById(R.id.colorChangingButton3).setVisibility(View.GONE);
        findViewById(R.id.search_card).setVisibility(View.GONE);

        overlayLayoutt.setVisibility(View.VISIBLE);

        LinearLayout linearLayout = findViewById(R.id.add_geo_btm);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        int customHeight = getResources().getDimensionPixelSize(R.dimen.height_btm_add);

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
                int offset = (int) ((slideOffset * 0.90 * layoutHeight));
                changePosLayout.setTranslationY(-offset);
            }
        });
    }


    public void showElements() {
        findViewById(R.id.menu_button).setVisibility(View.VISIBLE);
        findViewById(R.id.search_card).setVisibility(View.VISIBLE);
        findViewById(R.id.design_bottom_sheet).setVisibility(View.VISIBLE);
        findViewById(R.id.trafficbtn).setVisibility(View.VISIBLE);
        findViewById(R.id.landmarks).setVisibility(View.VISIBLE);
        findViewById(R.id.colorChangingButton4).setVisibility(View.VISIBLE);
        findViewById(R.id.colorChangingButton3).setVisibility(View.VISIBLE);
        findViewById(R.id.search_card).setVisibility(View.VISIBLE);


        LinearLayout overlayLayoutt = findViewById(R.id.add_geo_btm);
        overlayLayoutt.setVisibility(View.GONE);
    }

    private class NominatimTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> results = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();

            String url = "https://nominatim.openstreetmap.org/search?q=" + params[0] + "&format=json";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String displayName = jsonObject.optString("display_name", "");
                    results.add(displayName);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<String> results) {
            super.onPostExecute(results);
            // Update the RecyclerView with search results
            SearchResultsAdapter adapter = (SearchResultsAdapter) recyclerViewSearchResults.getAdapter();
            adapter.setData(results);
            adapter.notifyDataSetChanged();
        }
    }

    private static class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
        private ArrayList<String> data;

        public SearchResultsAdapter() {
            data = new ArrayList<>();
        }

        public void setData(ArrayList<String> newData) {
            data.clear();
            data.addAll(newData);
        }

        public void clear() {
            data.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String result = data.get(position);
            holder.textViewResult.setText(result);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewResult;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewResult = itemView.findViewById(R.id.textViewResult);
            }
        }
    }
}
