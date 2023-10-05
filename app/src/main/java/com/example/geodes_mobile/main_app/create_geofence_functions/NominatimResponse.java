package com.example.geodes_mobile.main_app.create_geofence_functions;
import com.google.gson.annotations.SerializedName;

public class NominatimResponse {

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lon")
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

}
