package com.sherdle.webtoapp.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.sherdle.webtoapp.R;
import com.sherdle.webtoapp.service.AlarmReceiver;
import com.sherdle.webtoapp.service.NotificationSoundService;
import com.sherdle.webtoapp.service.db.AppDatabase;
import com.sherdle.webtoapp.service.db.PrayerEntity;
import com.sherdle.webtoapp.service.woker.TestWorker;
import com.sherdle.webtoapp.util.FileUtils;
import com.sherdle.webtoapp.service.StopServiceReceiver;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QiblatActivity extends AppCompatActivity {

    // ID dari notifikasi
    private static final int NOTIFICATION_ID = 1;

    // ID dari channel notifikasi
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "Channel Name";
    private SharedPreferences prefs;
    private int jam = 0;
    private int menit = 0;
    private TextView tvData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiblat);

        prefs = getSharedPreferences("prayer_alarm", MODE_PRIVATE);

        tvData = findViewById(R.id.tv_data);
        Button btnShowNotif = findViewById(R.id.btn_show_notif);
        Button btnPickSound = findViewById(R.id.btn_pick_sound);
        Button btnAddJam = findViewById(R.id.btn_add_jam);
        Button btnMinJam = findViewById(R.id.btn_min_jam);
        Button btnAddMin = findViewById(R.id.btn_add_menit);
        Button btnMinMin = findViewById(R.id.btn_min_menit);
        TextView tvJam = findViewById(R.id.tv_jam);
        TextView tvMenit = findViewById(R.id.tv_menit);

        btnAddJam.setOnClickListener(view -> {
            jam++;
            tvJam.setText(String.valueOf(jam));
        });

        btnAddMin.setOnClickListener(view -> {
            menit++;
            tvMenit.setText(String.valueOf(menit));
        });

        btnMinJam.setOnClickListener(view -> {
            jam--;
            tvJam.setText(String.valueOf(jam));
        });

        btnMinMin.setOnClickListener(view -> {
            menit--;
            tvMenit.setText(String.valueOf(menit));
        });

        btnShowNotif.setOnClickListener(view -> setNotifWorker());
        btnPickSound.setOnClickListener(view -> getData());
    }

    private void checkAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            Toast.makeText(this, "Alarm sudah diatur", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Alarm belum diatur", Toast.LENGTH_SHORT).show();
        }
    }

    private void getData() {
        Executor executor = Executors.newSingleThreadExecutor();
        AppDatabase db = AppDatabase.getInstance(this);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        String currentDate = sdf.format(date);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<PrayerEntity> prayers = db.prayerDao().getPrayersByDate(currentDate);
                tvData.setText(String.valueOf(prayers));
            }
        });
    }

    private void setNotifWorker() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Menentukan waktu jadwal sholat (contoh: waktu Dhuhr)
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, jam); // Jam 12 siang
        calendar.set(Calendar.MINUTE, menit); // Menit ke-0
        calendar.set(Calendar.SECOND, 0); // Detik ke-0

        // Membuat Intent untuk mengeksekusi AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("prayerName", "Dhuhr"); // Menambahkan informasi nama sholat
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Mengatur alarm untuk waktu jadwal sholat Dhuhr
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void pickSound() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Pilih Suara"), 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            Uri selectedSoundUri = data.getData();
            String realPath = null;
            try {
                realPath = FileUtils.getRealPathFromURI(this, selectedSoundUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            prefs.edit().putString("sound_notif", realPath).apply();
            Toast.makeText(QiblatActivity.this, realPath, Toast.LENGTH_LONG).show();
            Log.d("File Picker", selectedSoundUri.toString());
            Log.d("File Picker", realPath);
        }
    }

    private void displayNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        Intent serviceIntent = new Intent(this, NotificationSoundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, StopServiceReceiver.class);
        stopIntent.setAction("ACTION_STOP_SERVICE");
        PendingIntent pStopSelf = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_alarm,
                "stop",
                pStopSelf)
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Judul Notifikasi")
                .setContentText("Isi Notifikasi")
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(action)
                .setAutoCancel(true);

        // Set channel untuk notifikasi (hanya untuk Android Oreo ke atas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        startService(serviceIntent);

        // Tampilkan notifikasi
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public String getRealPathFromURI(Uri uri) {
        String realPath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            realPath = getContentRealPath(uri);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            realPath = uri.getPath();
        }
        return realPath;
    }

    private String getContentRealPath(Uri contentUri) {
        String realPath = null;
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            ContentResolver contentResolver = getContentResolver();
            cursor = contentResolver.query(contentUri, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                realPath = cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return realPath;
    }
}