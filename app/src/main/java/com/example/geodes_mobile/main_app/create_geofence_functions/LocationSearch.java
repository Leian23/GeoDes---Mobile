package com.example.geodes_mobile.main_app.create_geofence_functions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.geodes_mobile.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

public class LocationSearch {


    public static void setupLocationSearch(final Context context, final MapView mapView, final SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                performNominatimSearch(query, new NominatimSearchCallback() {
                    @Override
                    public void onSearchResults(List<NominatimResponse> searchResultPoints) {
                        if (searchResultPoints != null && !searchResultPoints.isEmpty()) {
                            // Handle the list of search results here, e.g., update markers on the map
                            for (NominatimResponse nominatimResponse : searchResultPoints) {
                                double latitude = Double.parseDouble(nominatimResponse.getLatitude());
                                double longitude = Double.parseDouble(nominatimResponse.getLongitude());
                                GeoPoint searchResultPoint = new GeoPoint(latitude, longitude);
                                updateMarker(mapView, searchResultPoint, context); // Pass context here
                            }
                        } else {
                            // Handle no search results found
                            showToast(context, "No results found for: " + query);
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private static void performNominatimSearch(final String query, final NominatimSearchCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NomitanimHandler service = retrofit.create(NomitanimHandler.class);

        Call<List<NominatimResponse>> call = service.search(query, "json");

        call.enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NominatimResponse> searchResults = response.body();
                    callback.onSearchResults(searchResults);
                } else {
                    // Handle no search results found
                    callback.onSearchResults(new ArrayList<>()); // Empty list
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                // Handle failure
                callback.onSearchResults(new ArrayList<>()); // Empty list
            }
        });
    }

    private static void updateMarker(MapView mapView, GeoPoint point, Context context) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);

        // Set a custom marker icon from drawable resources
        Drawable customMarker = ContextCompat.getDrawable(context, R.drawable.marker_loc);
        marker.setIcon(customMarker);

        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }
    public interface NominatimSearchCallback {
        void onSearchResults(List<NominatimResponse> searchResultPoints);
    }
    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}