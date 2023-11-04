package com.example.geodes_mobile.main_app.homebtn_functions;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.geodes_mobile.R;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class TilesLayout extends Dialog {
    private RadioGroup radioGroup;
    private MapView mapView;
    private SharedPreferences sharedPreferences;

    public TilesLayout(Context context, MapView mapView) {
        super(context);
        this.mapView = mapView;
        sharedPreferences = context.getSharedPreferences("radio_state", Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiles_layout);

        radioGroup = findViewById(R.id.radioGroup);

        // Retrieve the saved radio button ID from SharedPreferences
        int selectedRadioButtonId = sharedPreferences.getInt("selected_radio_id", R.id.Standard);

        radioGroup.check(selectedRadioButtonId); // Set the selected radio button

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Save the selected radio button ID in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("selected_radio_id", checkedId);
                editor.apply();

                if (checkedId == R.id.Standard) {
                    mapView.setTileSource(TileSourceFactory.MAPNIK);
                    mapView.invalidate();
                } else if (checkedId == R.id.cycle) {
                    mapView.setTileSource(TileSourceFactory.ChartbundleENRH);
                    mapView.invalidate();
                } else if (checkedId == R.id.USGS) {
                    mapView.setTileSource(TileSourceFactory.USGS_SAT);
                    mapView.invalidate();
                } else if (checkedId == R.id.open_topo) {
                    mapView.setTileSource(TileSourceFactory.OpenTopo);
                    mapView.invalidate();
                }
            }
        });
    }
}
