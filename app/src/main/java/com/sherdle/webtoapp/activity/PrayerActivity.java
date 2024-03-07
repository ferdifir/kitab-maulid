package com.sherdle.webtoapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.service.LocationService;
import com.sherdle.webtoapp.service.db.PrayerEntity;
import com.sherdle.webtoapp.service.premium.AdMobHandler;
import com.sherdle.webtoapp.service.premium.PremiumManager;
import com.sherdle.webtoapp.service.sensor.BearingSensorManager;
import com.sherdle.webtoapp.service.woker.PrayerTimeWorker;
import com.sherdle.webtoapp.utils.Helper;
import com.sherdle.webtoapp.viewmodel.PrayerViewModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private AdMobHandler adMobHandler;
    private PremiumManager premiumManager;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);
        bearingSensorManager = new BearingSensorManager(this);
        prayerViewModel = new ViewModelProvider(this).get(PrayerViewModel.class);
        locationService = new LocationService(this);
        sharedPreferences = getSharedPreferences(Config.PREFS_KEY, MODE_PRIVATE);
        LinearLayout adContainerLayout = findViewById(R.id.adViewH);
        adMobHandler = new AdMobHandler(this);
        adMobHandler.loadBannerAd(this, adContainerLayout);
        premiumManager = new PremiumManager(this, new PremiumManager.PremiumListener() {
            @Override
            public void onPremiumPurchased() {
                adMobHandler.hideBannerAd();
            }
        });

        initView();
        getUserLocation();

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
            tvImsak.setText(today.getImsak());

            boolean isAfterIsya = Helper.isIsya(today.getIsha());
            LocalTime now = LocalTime.now();
            if (isAfterIsya) {
                String imsak = tomorrow.getImsak();
                int jamImsak = Integer.parseInt(imsak.substring(0, 2));
                int menitImsak = Integer.parseInt(imsak.substring(3));
                if (now.isAfter(LocalTime.of(23, 59))) {
                    long imsakTime = now.until(LocalTime.of(jamImsak, menitImsak), ChronoUnit.MILLIS);
                    startCountdown(imsakTime, 0);
                } else {
                    long toMidnight = now.until(LocalTime.of(23, 59), ChronoUnit.MILLIS);
                    long toImsak = LocalTime.MIDNIGHT.until(LocalTime.of(jamImsak, menitImsak), ChronoUnit.MILLIS);
                    startCountdown(toMidnight + toImsak, 0);
                }
            } else {
                List<String> prayerList = Helper.getPrayerList(today);
                for (int i = 0; i < prayerList.size(); i++) {
                    int hour = Integer.parseInt(prayerList.get(i).substring(0, 2));
                    int minute = Integer.parseInt(prayerList.get(i).substring(3));
                    LocalTime prayerSchedule = LocalTime.of(hour, minute);
                    if (prayerSchedule.isAfter(now)) {
                        startCountdown(now.until(prayerSchedule, ChronoUnit.MILLIS), i);
                        break;
                    }
                }
            }
        });
    }

    private void initButton() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        ivAlarmImsak.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Imsak",
                    sharedPreferences.getInt(Config.IMSAK_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.IMSAK_NOTIFICATION, 1 + which).apply();
                        initAlarmNotif();
                    }
            );
        });
        ivAlarmSubuh.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Subuh",
                    sharedPreferences.getInt(Config.SUBUH_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.SUBUH_NOTIFICATION, which).apply();
                        initAlarmNotif();
                    }
            );
        });
        ivAlarmTerbit.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Terbit",
                    sharedPreferences.getInt(Config.TERBIT_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.TERBIT_NOTIFICATION,1 +  which).apply();
                        initAlarmNotif();
                    }
            );
        });
        ivAlarmDhuhur.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Dhuhur",
                    sharedPreferences.getInt(Config.DZUHUR_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.DZUHUR_NOTIFICATION, which).apply();
                        initAlarmNotif();
                    }
            );
        });
        ivAlarmAshar.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Ashar",
                    sharedPreferences.getInt(Config.ASHAR_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.ASHAR_NOTIFICATION, which).apply();
                        initAlarmNotif();
                    }
            );
        });
        ivAlarmMaghrib.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Maghrib",
                    sharedPreferences.getInt(Config.MAGHRIB_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.MAGHRIB_NOTIFICATION, which).apply();
                        initAlarmNotif();
                    }
            );
        });
        ivAlarmIsya.setOnClickListener(v -> {
            showRadioButtonDialog(
                    "Notifikasi Isya",
                    sharedPreferences.getInt(Config.ISYA_NOTIFICATION, 3),
                    (dialog, which) -> {
                        sharedPreferences.edit().putInt(Config.ISYA_NOTIFICATION, which).apply();
                        initAlarmNotif();
                        if (which == 1) {
                            fileActivityResultLauncher.launch(intent);
                        }
                    }
            );
        });
    }

    ActivityResultLauncher<Intent> fileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResultCallback<ActivityResult>) result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    double lat = data.getDoubleExtra("lat", 0.0);
                    double lon = data.getDoubleExtra("lon", 0.0);
                    setNewData(lat, lon);
                }
            });

    public void showRadioButtonDialog(String title, int checkedItem, DialogInterface.OnClickListener listener) {
        String[] items = title.contains("Imsak") || title.contains("Terbit") ?
                new String[]{"Suara standar alarm", "Suara standar notifikasi", "Tanpa suara (hanya notifikasi)", "Nonaktifkan notifikasi"} :
                new String[]{"Suara adzan", "Suara standar alarm", "Suara standar notifikasi", "Tanpa suara (hanya notifikasi)", "Nonaktifkan notifikasi"};

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
        bearingSensorManager.setOnBearingChangeListener(new BearingSensorManager.OnBearingChangeListener() {
            @Override
            public void onBearingChanged(float bearing) {
                double qiblaDirection = calculateQiblaDirection(latitude, longitude, bearing);
                ivCompass.setRotation((float) qiblaDirection);
            }
        });
    }

    private void startCountdown(Long nextPrayerSchedule, int index) {
        Log.d("Countdown", String.valueOf(nextPrayerSchedule));
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
        locationService.requestLocationUpdates();
        locationService.getLastKnownLocation(new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLastLocation();
                if (location != null) {
                    sharedPreferences.edit().putFloat(Config.LAT_KEY, (float) location.getLatitude()).apply();
                    sharedPreferences.edit().putFloat(Config.LON_KEY, (float) location.getLongitude()).apply();
                    String address = locationService.getAddressFromCoordinates(location.getLatitude(), location.getLongitude());
                    currentAddress = Helper.getStringBetweenCommas(address, 3, 4);
                    tvLocation.setText(currentAddress);
                    initToolbar();
                    getQiblat(location.getLatitude(), location.getLongitude());
                }
            }
        });
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
        initAlarmNotif();
    }

    private void initAlarmNotif() {
        int imsak = sharedPreferences.getInt(Config.IMSAK_NOTIFICATION, 3);
        int subuh = sharedPreferences.getInt(Config.SUBUH_NOTIFICATION, 3);
        int terbit = sharedPreferences.getInt(Config.TERBIT_NOTIFICATION, 3);
        int dhuhur = sharedPreferences.getInt(Config.DZUHUR_NOTIFICATION, 3);
        int ashar = sharedPreferences.getInt(Config.ASHAR_NOTIFICATION, 3);
        int maghrib = sharedPreferences.getInt(Config.MAGHRIB_NOTIFICATION, 3);
        int isya = sharedPreferences.getInt(Config.ISYA_NOTIFICATION, 3);
        ivAlarmImsak.setImageDrawable(getResources().getDrawable(getDrawableNotif(imsak)));
        ivAlarmSubuh.setImageDrawable(getResources().getDrawable(getDrawableNotif(subuh)));
        ivAlarmTerbit.setImageDrawable(getResources().getDrawable(getDrawableNotif(terbit)));
        ivAlarmDhuhur.setImageDrawable(getResources().getDrawable(getDrawableNotif(dhuhur)));
        ivAlarmAshar.setImageDrawable(getResources().getDrawable(getDrawableNotif(ashar)));
        ivAlarmMaghrib.setImageDrawable(getResources().getDrawable(getDrawableNotif(maghrib)));
        ivAlarmIsya.setImageDrawable(getResources().getDrawable(getDrawableNotif(isya)));
    }

    private int getDrawableNotif(int index) {
        switch (index) {
            case 0:
                return R.drawable.ic_alarm;
            case 1:
                return R.drawable.ic_notifications_active;
            case 2:
                return R.drawable.ic_notifications;
            case 3:
                return R.drawable.ic_notifications_none;
            case 4:
                return R.drawable.ic_notifications_off;
            default:
                return R.drawable.ic_alarm;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_prayer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.location) {
            showLocationOptionDialog();
        } else if (item.getItemId() == R.id.calendar) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.settings) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Log.d("Sound URI", soundUri.toString());
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(PrayerActivity.this, soundUri);
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLocationOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Lokasi");
        final String[] items = {"Gunakan Posisi Saya Sekarang", "Pilih Secara Manual dengan Map"};
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0:
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    setNewData(lat, lon);
                    break;
                case 1:
                    Intent intent = new Intent(PrayerActivity.this, MapActivity.class);
                    mapActivityResultLauncher.launch(intent);
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setNewData(double lat, double lon) {
        prayerViewModel.deleteAllData();
        prayerViewModel.getPrayerSchedule(lat, lon);
        prayerViewModel.prayers.observe(this, prayerEntity -> {
            WorkManager.getInstance(PrayerActivity.this).cancelAllWorkByTag(Config.PRAYER_WORKER_TAG);
            long delay = Helper.getDelayNextPrayer(prayerEntity.get(0),prayerEntity.get(1).getImsak());
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(PrayerTimeWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .addTag(Config.PRAYER_WORKER_TAG)
                    .build();
            WorkManager.getInstance(PrayerActivity.this).enqueue(workRequest);
            PrayerActivity.this.recreate();
        });
    }

    ActivityResultLauncher<Intent> mapActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResultCallback<ActivityResult>) result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    double lat = data.getDoubleExtra("lat", 0.0);
                    double lon = data.getDoubleExtra("lon", 0.0);
                    setNewData(lat, lon);
                }
            });

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
        String textDesc = prayer == 0 || prayer == 2 ? "Menuju Waktu " + Helper.getPrayerName(prayer) : "Menuju Waktu Sholat " + Helper.getPrayerName(prayer);
        tvCountdownDesc.setText(textDesc);
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