package com.sherdle.webtoapp.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.service.LocationService;
import com.sherdle.webtoapp.service.api.response.schedule.Timings;
import com.sherdle.webtoapp.service.db.PrayerEntity;
import com.sherdle.webtoapp.service.sensor.BearingSensorManager;
import com.sherdle.webtoapp.utils.Helper;
import com.sherdle.webtoapp.viewmodel.PrayerViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrayerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvHijrDate;
    private TextView tvCountdownDesc;
    private TextView tvCountdown;
    private TextView tvLocation;
    private TextView tvImsak;
    private TextView tvSubuh;
    private TextView tvTerbit;
    private TextView tvDhuhur;
    private TextView tvAshar;
    private TextView tvMaghrib;
    private TextView tvIsya;
    private ImageView ivAlarmImsak, ivAlarmSubuh, ivAlarmTerbit, ivAlarmDhuhur, ivAlarmAshar, ivAlarmMaghrib, ivAlarmIsya;
    private PrayerViewModel prayerViewModel;
    private LocationService locationService;
    private String currentAddress = "";
    private CountDownTimer countDownTimer;
    private BearingSensorManager bearingSensorManager;
    private ImageView ivCompass;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private static final double KAABA_LATITUDE = 21.4225;
    private static final double KAABA_LONGITUDE = 39.8262;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);
        prayerViewModel = new ViewModelProvider(this).get(PrayerViewModel.class);
        locationService = new LocationService(this);
        sharedPreferences = getSharedPreferences("prayer_alarm", MODE_PRIVATE);

        initView();
        getUserLocation();
        initToolbar();
        getDate();
        initButton();
        getPrayerSchedule();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        });
    }

    private void getPrayerSchedule() {
        prayerViewModel.getPrayerSchedule().observe(this, listData -> {
            PrayerEntity today = listData.get(0);
            PrayerEntity tomorrow = listData.get(1);
            tvIsya.setText(today.getIsha());
            tvAshar.setText(today.getAsr());
            tvDhuhur.setText(today.getDhuhr());
            tvMaghrib.setText(today.getMaghrib());
            tvTerbit.setText(today.getSunrise());
            tvSubuh.setText(today.getFajr());

            boolean isAfterIsya = Helper.isIsya(today.getIsha());
            LocalTime now = LocalTime.now();
            if (isAfterIsya) {
                String imsak = tomorrow.getImsak();
                if (now.isAfter(LocalTime.MIDNIGHT)) {
                    Long imsakTime = Helper.convertTimeStringToMillis(imsak);
                    startCountdown(imsakTime, 0);
                } else {
                    Long toMidnight = Helper.convertTimeStringToMillis("23:59");
                    Long toImsak = Helper.convertTimeStringToMillis(imsak);
//                    startCountdown();
                }
            } else {
                long diff = Helper.getSelisihWaktuSholatTerdekat(Helper.getPrayerList(today));
                startCountdown(diff, 4);
            }
        });
    }

    private void initButton() {
        ivAlarmImsak.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Imsak",
                    sharedPreferences.getInt(Config.IMSAK_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.IMSAK_NOTIFICATION, which).apply();
                    }
            );
        });
        ivAlarmSubuh.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Subuh",
                    sharedPreferences.getInt(Config.SUBUH_NOTIFICATION, 0),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.SUBUH_NOTIFICATION, which).apply();
                    }
            );
        });
        ivAlarmTerbit.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Terbit",
                    sharedPreferences.getInt(Config.TERBIT_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.TERBIT_NOTIFICATION, which).apply();
                    }
            );
        });
        ivAlarmDhuhur.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Dhuhur",
                    sharedPreferences.getInt(Config.DZUHUR_NOTIFICATION, 0),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.DZUHUR_NOTIFICATION, which).apply();
                    }
            );
        });
        ivAlarmAshar.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Ashar",
                    sharedPreferences.getInt(Config.ASHAR_NOTIFICATION, 0),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.ASHAR_NOTIFICATION, which).apply();
                    }
            );
        });
        ivAlarmMaghrib.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Maghrib",
                    sharedPreferences.getInt(Config.MAGHRIB_NOTIFICATION, 0),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.MAGHRIB_NOTIFICATION, which).apply();
                    }
            );
        });
        ivAlarmIsya.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Isya",
                    sharedPreferences.getInt(Config.ISYA_NOTIFICATION, 0),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.ISYA_NOTIFICATION, which).apply();
                    }
            );
        });
    }

    public void showRadioButtonDialog(String title, int checkedItem, DialogInterface.OnClickListener listener) {
        String[] items = {"Suara adzan", "Suara standar alarm", "Suara standar notifikasi", "Tanpa suara (hanya notifikasi)", "Nonaktifkan notifikasi"};
        if (title.contains("Imsak") || title.contains("Terbit")) {
            items = new String[]{"Suara standar alarm", "Suara standar notifikasi", "Tanpa suara (hanya notifikasi)", "Nonaktifkan notifikasi"};
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setSingleChoiceItems(items, checkedItem, listener);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getQiblat(double latitude, double longitude) {
        bearingSensorManager = new BearingSensorManager(this);
        bearingSensorManager.setOnBearingChangeListener(new BearingSensorManager.OnBearingChangeListener() {
            @Override
            public void onBearingChanged(float bearing) {
                double qiblaDirection = calculateQiblaDirection(latitude, longitude, bearing);
                ivCompass.setRotation((float) qiblaDirection);
            }
        });
    }

    private void startCountdown(Long nextPrayerSchedule, int index) {
        countDownTimer = new CountDownTimer(nextPrayerSchedule, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountdownText(millisUntilFinished, index);
            }

            @Override
            public void onFinish() {
                tvCountdownDesc.setText("Waktu Sholat " + Helper.getPrayerName(index) + " telah tiba");
                tvCountdown.setText("0 menit");
            }
        };

        countDownTimer.start();
    }

    private void getUserLocation() {
        Location location = locationService.getLastKnownLocation();
        if (location != null) {
            String address = locationService.getAddressFromCoordinates(location.getLatitude(), location.getLongitude());
            currentAddress = Helper.getStringBetweenCommas(address, 3, 4);
            tvLocation.setText(currentAddress);
            getQiblat(location.getLatitude(), location.getLongitude());
        }
    }

    private void getDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat sdfApi = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(date);
        prayerViewModel.getHijrDate(sdfApi.format(date));
        prayerViewModel.getHijrDateValue().observe(this, hijrDate -> {
            tvHijrDate.setText(hijrDate + "/" + currentDate);
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
        TextView tvQiblat = findViewById(R.id.tv_qiblat);
        tvHijrDate = findViewById(R.id.tv_hijr_date);
        tvLocation = findViewById(R.id.tv_location);
        tvImsak = findViewById(R.id.tv_imsak);
        tvSubuh = findViewById(R.id.tv_subuh);
        tvTerbit = findViewById(R.id.tv_terbit);
        tvDhuhur = findViewById(R.id.tv_dhuhur);
        tvAshar = findViewById(R.id.tv_ashar);
        tvMaghrib = findViewById(R.id.tv_maghrib);
        tvIsya = findViewById(R.id.tv_isya);
        ivCompass = findViewById(R.id.ivCompass);
        ivAlarmImsak = findViewById(R.id.iv_alarm_imsak);
        ivAlarmSubuh = findViewById(R.id.iv_alarm_subuh);
        ivAlarmTerbit = findViewById(R.id.iv_alarm_terbit);
        ivAlarmDhuhur = findViewById(R.id.iv_alarm_dzuhur);
        ivAlarmAshar = findViewById(R.id.iv_alarm_ashar);
        ivAlarmMaghrib = findViewById(R.id.iv_alarm_maghrib);
        ivAlarmIsya = findViewById(R.id.iv_alarm_isya);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_prayer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_prayer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateCountdownText(long millisUntilFinished, int prayer) {
        int minutes = (int) (millisUntilFinished / 1000) / 60;

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format("%d jam %d menit", hours, remainingMinutes);
        } else {
            timeLeftFormatted = String.format("%d menit", remainingMinutes);
        }

        tvCountdown.setText(timeLeftFormatted);
        tvCountdownDesc.setText("Menuju Waktu Sholat " + Helper.getPrayerName(prayer));
    }

    private double calculateQiblaDirection(double userLatitude, double userLongitude, float bearing) {
        double kaabaLatitude = Math.toRadians(KAABA_LATITUDE);
        double kaabaLongitude = Math.toRadians(KAABA_LONGITUDE);
        double userLatRad = Math.toRadians(userLatitude);
        double userLongRad = Math.toRadians(userLongitude);

        double deltaLong = kaabaLongitude - userLongRad;

        double y = Math.sin(deltaLong) * Math.cos(kaabaLatitude);
        double x = Math.cos(userLatRad) * Math.sin(kaabaLatitude) -
                Math.sin(userLatRad) * Math.cos(kaabaLatitude) * Math.cos(deltaLong);

        double qiblaDirectionRad = Math.atan2(y, x);
        double qiblaDirectionDeg = Math.toDegrees(qiblaDirectionRad);

        qiblaDirectionDeg -= bearing;

        return (qiblaDirectionDeg + 360) % 360;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        bearingSensorManager.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        bearingSensorManager.stop();
        super.onPause();
    }
}