package com.sherdle.webtoapp.service.alarm;

import static android.content.Context.MODE_PRIVATE;
import static com.sherdle.webtoapp.utils.Helper.getTomorrowDate;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.activity.MainActivity;
import com.sherdle.webtoapp.service.NotificationSoundService;
import com.sherdle.webtoapp.service.StopServiceReceiver;
import com.sherdle.webtoapp.service.db.AppDatabase;
import com.sherdle.webtoapp.service.db.PrayerEntity;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DhuhurAlarmManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        displayNotification(context);
        setNextAlarm(context);
    }

    private void displayNotification(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Config.PREFS_KEY, MODE_PRIVATE);

        Intent intent = new Intent(context, MainActivity.class);
        Intent serviceIntent = new Intent(context, NotificationSoundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(context, StopServiceReceiver.class);
        stopIntent.setAction("ACTION_STOP_SERVICE");
        PendingIntent pStopSelf = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri adzanUri = Uri.parse(prefs.getString(Config.DHUHUR_URI, ""));
        int notifOpt = prefs.getInt(Config.DZUHUR_NOTIFICATION, 3);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_alarm,
                "stop",
                pStopSelf)
                .build();

        switch (notifOpt) {
            case 0:
                serviceIntent.setData(adzanUri);
                context.startService(serviceIntent);
                NotificationCompat.Builder firstbuilder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Sholat Dhuhur")
                        .setContentText("Sudah masuk waktu sholat Dhuhur")
                        .setContentIntent(pendingIntent)
                        .setSound(soundUri)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(action)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationManager firstNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                firstNotifManager.notify(1, firstbuilder.build());
                break;
            case 1:
                serviceIntent.setData(alarmUri);
                context.startService(serviceIntent);
                NotificationCompat.Builder secondbuilder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Sholat Dhuhur")
                        .setContentText("Sudah masuk waktu sholat Dhuhur")
                        .setContentIntent(pendingIntent)
                        .setSound(soundUri)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(action)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationManager firstnotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                firstnotificationManager.notify(1, secondbuilder.build());
                break;
            case 2:
                serviceIntent.setData(soundUri);
                context.startService(serviceIntent);
                NotificationCompat.Builder thirdbuilder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Sholat Dhuhur")
                        .setContentText("Sudah masuk waktu sholat Dhuhur")
                        .setContentIntent(pendingIntent)
                        .setSound(soundUri)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager secondnotificationManager = context.getSystemService(NotificationManager.class);
                    secondnotificationManager.createNotificationChannel(channel);
                }
                NotificationManager secondNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                secondNotifManager.notify(1, thirdbuilder.build());
                break;
            case 3:
                NotificationCompat.Builder fourthbuilder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Sholat Dhuhur")
                        .setContentText("Sudah masuk waktu sholat Dhuhur")
                        .setContentIntent(pendingIntent)
                        .setSound(soundUri)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationManager thirdNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                thirdNotifManager.notify(1, fourthbuilder.build());
                break;
            case 4:
                Log.d("Notification", "Sudah Masuk Waktu Sholat Dhuhur");
                break;
            default:
                Log.d("Notification", "Sudah Masuk Waktu Sholat Dhuhur");
                break;
        }
    }

    private void setNextAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DhuhurAlarmManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AppDatabase db = AppDatabase.getInstance(context);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<PrayerEntity> prayerEntities = db.prayerDao().getPrayersByDate(getTomorrowDate());
                int jam = Integer.parseInt(prayerEntities.get(0).getDhuhr().substring(0,2));
                int menit = Integer.parseInt(prayerEntities.get(0).getDhuhr().substring(3));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, jam);
                calendar.set(Calendar.MINUTE, menit);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        });
    }
}
