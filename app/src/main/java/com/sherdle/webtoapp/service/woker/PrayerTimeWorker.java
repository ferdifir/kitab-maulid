package com.sherdle.webtoapp.service.woker;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.activity.MainActivity;
import com.sherdle.webtoapp.service.NotificationSoundService;
import com.sherdle.webtoapp.service.StopServiceReceiver;
import com.sherdle.webtoapp.service.db.AppDatabase;
import com.sherdle.webtoapp.service.db.PrayerEntity;
import com.sherdle.webtoapp.utils.Helper;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PrayerTimeWorker extends Worker {

    private final Context context;
    public PrayerTimeWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Pair<List<PrayerEntity>, List<PrayerEntity>> data = scheduleNextAlarm();
        showNotification(data.first.get(0), data.second.get(0).getImsak());
        return Result.success();
    }

    private void showNotification(PrayerEntity todaySchedule, String imsakTomorrow) {
        SharedPreferences prefs = context.getSharedPreferences(Config.PREFS_KEY, MODE_PRIVATE);
        Uri soundUri = Uri.parse(prefs.getString(Config.ASHR_URI, ""));

        int index = 0;
        List<String> prayerList = Helper.getPrayerList(todaySchedule);
        for (int i = 0; i < prayerList.size(); i++) {
            int hour = Integer.parseInt(prayerList.get(i).substring(0,2));
            int minutes = Integer.parseInt(prayerList.get(i).substring(3));
            LocalTime schedule = LocalTime.of(hour, minutes);
            if (schedule.equals(LocalTime.now())) {
                index = i;
                break;
            }
        }

        String sholat = Helper.getPrayerName(index);

        Intent intent = new Intent(context, MainActivity.class);
        Intent serviceIntent = new Intent(context, NotificationSoundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Config.ASHAR_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(context, StopServiceReceiver.class);
        stopIntent.setAction("ACTION_STOP_SERVICE");
        PendingIntent pStopSelf = PendingIntent.getBroadcast(context, Config.ASHAR_REQ_CODE, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_alarm,
                "stop",
                pStopSelf)
                .build();

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Sholat " + sholat)
                .setContentText("Sudah masuk waktu sholat " + sholat)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(action)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        serviceIntent.setData(soundUri);
        context.startService(serviceIntent);

        NotificationManager notifManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(1, notifBuilder.build());
    }

    private Pair<List<PrayerEntity>, List<PrayerEntity>> scheduleNextAlarm() {
        Executor executor = Executors.newSingleThreadExecutor();
        AppDatabase db = AppDatabase.getInstance(context);
        WorkManager.getInstance(context).cancelAllWorkByTag(Config.PRAYER_WORKER_TAG);
        String today = getFormattedDate(Calendar.getInstance().getTime());
        String tomorrow = getFormattedDate(getTomorrowDate());

        List<PrayerEntity> todayList = new ArrayList<>();
        List<PrayerEntity> tomorrowList = new ArrayList<>();

        executor.execute(() -> {
            todayList.addAll(db.prayerDao().getPrayersByDate(today));
            tomorrowList.addAll(db.prayerDao().getPrayersByDate(tomorrow));

            long delay = Helper.getDelayNextPrayer(todayList.get(0), tomorrowList.get(0).getImsak());

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(PrayerTimeWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .addTag(Config.PRAYER_WORKER_TAG)
                    .build();

            WorkManager.getInstance(context).enqueue(workRequest);
        });

        return new Pair<>(todayList, tomorrowList);
    }


    private String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private Date getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

}