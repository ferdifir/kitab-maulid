package com.sherdle.webtoapp.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.service.LocationService;
import com.sherdle.webtoapp.viewmodel.PrayerViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrayerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvHijrDate, tvMasehiDate, tvCountdownDesc, tvCountdown, tvLocation;
    private PrayerViewModel prayerViewModel;
    private LocationService locationService;
    private String currentAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);
        prayerViewModel = new ViewModelProvider(this).get(PrayerViewModel.class);
        locationService = new LocationService(this);

        initView();
        getUserLocation();
        initToolbar();
        getDate();
    }

    private void getUserLocation() {
        Location location = locationService.getLastKnownLocation();
        if (location != null) {
            String address = locationService.getAddressFromCoordinates(location.getLatitude(), location.getLongitude());
            currentAddress = getStringBetweenCommas(address, 3, 4);
            tvLocation.setText(currentAddress);
        }
    }

    private void getDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat sdfApi = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(date);
        prayerViewModel.getHijrDate(sdfApi.format(date));
        tvMasehiDate.setText(currentDate);
        prayerViewModel.getHijrDateValue().observe(this, hijrDate -> {
            tvHijrDate.setText(hijrDate);
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(currentAddress);
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_prayer);
        tvCountdown = findViewById(R.id.tv_countdown);
        tvCountdownDesc = findViewById(R.id.tv_countdown_desc);
        tvMasehiDate = findViewById(R.id.tv_masehi_date);
        tvHijrDate = findViewById(R.id.tv_hijr_date);
        tvLocation = findViewById(R.id.tv_location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_prayer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private static String getStringBetweenCommas(String input, int startComma, int endComma) {
        String[] parts = input.split(",");

        if (startComma > 0 && endComma <= parts.length) {
            StringBuilder result = new StringBuilder();
            for (int i = startComma; i < endComma; i++) {
                result.append(parts[i]).append(",");
            }

            result.deleteCharAt(result.length() - 1);

            return result.toString();
        } else {
            return "Invalid indices";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}