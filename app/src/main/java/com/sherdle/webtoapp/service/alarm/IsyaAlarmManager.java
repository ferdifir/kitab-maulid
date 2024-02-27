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
import com.sherdle.webtoapp.utils.Helper;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class IsyaAlarmManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        displayNotification(context);
        setNextAlarm(context);
    }

    private void displayNotification(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Config.PREFS_KEY, MODE_PRIVATE);

        Intent intent = new Intent(context, MainActivity.class);
        Intent serviceIntent = new Intent(context, NotificationSoundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Config.ISYA_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(context, StopServiceReceiver.class);
        stopIntent.setAction("ACTION_STOP_SERVICE");
        PendingIntent pStopSelf = PendingIntent.getBroadcast(context, Config.ISYA_REQ_CODE, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri adzanUri = Uri.parse(prefs.getString(Config.ISYA_URI, ""));
        int notifOpt = prefs.getInt(Config.ISYA_NOTIFICATION, 3);

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
                        .setContentTitle("Sholat Isya")
                        .setContentText("Sudah masuk waktu sholat Isya")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(action)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationManager firstNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                firstNotifManager.notify(prefs.getInt(Config.NOTIF_ID_KEY, 1), firstbuilder.build());
                prefs.edit().putInt(Config.NOTIF_ID_KEY, prefs.getInt(Config.NOTIF_ID_KEY, 1) + 1);
                break;
            case 1:
                serviceIntent.setData(alarmUri);
                context.startService(serviceIntent);
                NotificationCompat.Builder secondbuilder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Sholat Isya")
                        .setContentText("Sudah masuk waktu sholat Isya")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(action)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationManager firstnotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                firstnotificationManager.notify(prefs.getInt(Config.NOTIF_ID_KEY, 1), secondbuilder.build());
                prefs.edit().putInt(Config.NOTIF_ID_KEY, prefs.getInt(Config.NOTIF_ID_KEY, 1) + 1);
                break;
            case 2:
                serviceIntent.setData(soundUri);
                context.startService(serviceIntent);
                NotificationCompat.Builder thirdbuilder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Sholat Isya")
                        .setContentText("Sudah masuk waktu sholat Isya")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager secondnotificationManager = context.getSystemService(NotificationManager.class);
                    secondnotificationManager.createNotificationChannel(channel);
                }
                NotificationManager secondNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                secondNotifManager.notify(prefs.getInt(Config.NOTIF_ID_KEY, 1), thirdbuilder.build());
                prefs.edit().putInt(Config.NOTIF_ID_KEY, prefs.getInt(Config.NOTIF_ID_KEY, 1) + 1);
                break;
            case 3:
                NotificationCompat.Builder fourthbuilder = new NotificationCompat.Builder(context, "channel_id")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Sholat Isya")
                        .setContentText("Sudah masuk waktu sholat Isya")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationManager thirdNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                thirdNotifManager.notify(prefs.getInt(Config.NOTIF_ID_KEY, 1), fourthbuilder.build());
                prefs.edit().putInt(Config.NOTIF_ID_KEY, prefs.getInt(Config.NOTIF_ID_KEY, 1) + 1);
                break;
            case 4:
                Log.d("Notification", "Sudah Masuk Waktu Sholat Isya");
                break;
            default:
                Log.d("Notification", "Sudah Masuk Waktu Sholat Isya");
                break;
        }
    }

    private void setNextAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, IsyaAlarmManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Config.ISYA_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        AppDatabase db = AppDatabase.getInstance(context);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<PrayerEntity> prayerEntities = db.prayerDao().getPrayersByDate(getTomorrowDate());

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        Helper.getTimeMillis(prayerEntities.get(0).getIsha(), true),
                        pendingIntent);
            }
        });
    }
}