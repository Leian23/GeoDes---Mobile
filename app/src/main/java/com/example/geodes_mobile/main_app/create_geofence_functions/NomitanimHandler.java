package com.example.geodes_mobile.main_app.create_geofence_functions;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface NomitanimHandler
{
    @GET("search")
    Call<List<NominatimResponse>> search(@Query("q") String query, @Query("format") String format);

}
