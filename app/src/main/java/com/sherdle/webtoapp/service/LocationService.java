package com.sherdle.webtoapp.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService {

    private static final String TAG = LocationService.class.getSimpleName();

    private final Context context;
    private final LocationManager locationManager;
    private final LocationListener locationListener;

    public LocationService(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = new MyLocationListener();
    }

    public Location getLastKnownLocation() {
        // Mendapatkan lokasi terakhir yang diketahui dari provider
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission not granted");
            return null;
        }

        Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // Memilih lokasi yang lebih akurat
        if (lastKnownLocationGPS != null && lastKnownLocationNetwork != null) {
            if (lastKnownLocationGPS.getAccuracy() <= lastKnownLocationNetwork.getAccuracy()) {
                return lastKnownLocationGPS;
            } else {
                return lastKnownLocationNetwork;
            }
        } else if (lastKnownLocationGPS != null) {
            return lastKnownLocationGPS;
        } else if (lastKnownLocationNetwork != null) {
            return lastKnownLocationNetwork;
        } else {
            Log.e(TAG, "Last known location is null");
            return null;
        }
    }

    public String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address.getAddressLine(i)).append(", ");
                }
                result = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void requestLocationUpdates() {
        // Meminta pembaruan lokasi dari provider
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission not granted");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public void removeLocationUpdates() {
        // Menghentikan pembaruan lokasi
        locationManager.removeUpdates(locationListener);
    }

    private static class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // Aksi yang diambil saat lokasi berubah
            Log.d(TAG, "Location changed: " + location.getLatitude() + ", " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Aksi yang diambil saat status provider berubah
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Aksi yang diambil saat provider diaktifkan
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Aksi yang diambil saat provider dinonaktifkan
        }
    }
}
