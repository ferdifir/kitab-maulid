package com.sherdle.webtoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.service.LocationService;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapView mapView = findViewById(R.id.mapView);
        EditText etAlamat = findViewById(R.id.et_alamat);
        FloatingActionButton fabOk = findViewById(R.id.fabtn_ok);
        ImageView ivMarker = findViewById(R.id.marker);
        ProgressBar loading = findViewById(R.id.loading);

        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        LocationService locationService = new LocationService(this);
        SharedPreferences prefs = getSharedPreferences(Config.PREFS_KEY, MODE_PRIVATE);

        double savedLatitude = prefs.getFloat(Config.LAT_KEY, 0.0f);
        double savedLongitude = prefs.getFloat(Config.LON_KEY, 0.0f);

        locationService.requestLocationUpdates();
        locationService.getLastKnownLocation(new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                String alamat;
                GeoPoint currentLocation;
                Location location = locationResult.getLastLocation();
                if (savedLatitude != 0.0 && savedLongitude != 0.0) {
                    alamat = locationService.getAddressFromCoordinates(savedLatitude, savedLongitude);
                    currentLocation = new GeoPoint(savedLatitude, savedLongitude);
                } else {
                    alamat = locationService.getAddressFromCoordinates(location.getLatitude(), location.getLongitude());
                    currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                }
                etAlamat.setText(alamat);
                mapView.setTileSource(TileSourceFactory.MAPNIK);
                mapView.setBuiltInZoomControls(false);
                mapView.setMultiTouchControls(true);
                mapView.getController().setZoom(17.0);
                mapView.getController().setCenter(currentLocation);
            }
        });

        mapView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                loading.setVisibility(View.GONE);
                String selectedAlamat = locationService.getAddressFromCoordinates(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                etAlamat.setText(selectedAlamat);
            } else {
                loading.setVisibility(View.VISIBLE);
                etAlamat.setText("Sedang mendapatkan alamat terbaru..");
            }
            return false;
        });

        fabOk.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("lat", mapView.getMapCenter().getLatitude());
            returnIntent.putExtra("lon", mapView.getMapCenter().getLongitude());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

}