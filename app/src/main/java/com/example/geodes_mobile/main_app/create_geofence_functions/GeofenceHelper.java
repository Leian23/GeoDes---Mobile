package com.example.geodes_mobile.main_app.create_geofence_functions;

import com.google.android.gms.location.Geofence;

import org.osmdroid.util.GeoPoint;

import java.util.Random;
import java.util.UUID;

public class GeofenceHelper {

    public GeofenceHelper() {
    }


    public Geofence createEntryGeofence(GeoPoint latLng, float radius, String requestId) {
        return new Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(latLng.getLatitude(), latLng.getLongitude(), radius)
                .setExpirationDuration(20 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
    }



    public Geofence createExitGeofence(GeoPoint latLng, float radius, String requestId) {
        return new Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(latLng.getLatitude(), latLng.getLongitude(), radius)
                .setExpirationDuration(20 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }


    public String innerVal() {
        int minLength = 1;
        int maxLength = 2000;

        // Generate a random number with a length between 1 and 1000
        int randomNumber = generateRandomNumber(minLength, maxLength);



        return String.valueOf(randomNumber);
    }

    public String OuterVal() {
        int minLength = 2001;
        int maxLength = 4000;

        // Generate a random number with a length between 1001 and 2000
        int randomNumber = generateRandomNumber(minLength, maxLength);



        return String.valueOf(randomNumber);
    }

    private int generateRandomNumber(int minLength, int maxLength) {
        Random random = new Random();
        int range = maxLength - minLength + 1;
        int randomNumber = random.nextInt(range) + minLength;
        return randomNumber;
    }


}
