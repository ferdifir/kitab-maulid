package com.sherdle.webtoapp.service.woker;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.service.api.ApiService;
import com.sherdle.webtoapp.service.api.RetrofitClient;
import com.sherdle.webtoapp.service.api.response.schedule.PrayersResponse;
import com.sherdle.webtoapp.utils.Helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrayerTimeWorker extends Worker {

    public PrayerTimeWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        showPrayerNotification("Sudah masuk waktu sholat");
        return Result.success();
    }

    private void getPrayerSchedule() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("prayer_alarm", MODE_PRIVATE);
        double lat = prefs.getFloat(Config.LAT_KEY, 0L);
        double lon = prefs.getFloat(Config.LON_KEY, 0L);
        String currentDateTime = Helper.getCurrentDateTime(true);
        int year = Integer.parseInt(currentDateTime.substring(6,10));
        int month = Integer.parseInt(currentDateTime.substring(3,5));
        ApiService api = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        api.getPrayerSchedule(year, month, lat, lon, 2).enqueue(new Callback<PrayersResponse>() {
            @Override
            public void onResponse(Call<PrayersResponse> call, Response<PrayersResponse> response) {
                if (response.isSuccessful()) {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                }
            }

            @Override
            public void onFailure(Call<PrayersResponse> call, Throwable t) {

            }
        });
    }

    private void showPrayerNotification(String s) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "channelId")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Jadwal Sholat")
                        .setContentText(s)
                        .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void scheduleNextPrayerWork(long delayMillis) {
        OneTimeWorkRequest nextPrayerWorkRequest =
                new OneTimeWorkRequest.Builder(PrayerTimeWorker.class)
                        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueue(nextPrayerWorkRequest);
    }
}