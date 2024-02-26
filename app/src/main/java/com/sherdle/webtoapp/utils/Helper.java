package com.sherdle.webtoapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.sherdle.webtoapp.service.alarm.AsrAlarmManager;
import com.sherdle.webtoapp.service.alarm.DhuhurAlarmManager;
import com.sherdle.webtoapp.service.alarm.ImsakAlarmManager;
import com.sherdle.webtoapp.service.alarm.IsyaAlarmManager;
import com.sherdle.webtoapp.service.alarm.MaghribAlarmManager;
import com.sherdle.webtoapp.service.alarm.SubuhAlarmManager;
import com.sherdle.webtoapp.service.alarm.TerbitAlarmManager;
import com.sherdle.webtoapp.service.api.response.schedule.Timings;
import com.sherdle.webtoapp.service.db.PrayerEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Helper {

    public static String getTomorrowDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return tomorrow.format(formatter);
    }

    public static String getPrayerName(int index) {
        switch (index) {
            case 0:
                return "Imsak";
            case 1:
                return "Subuh";
            case 2:
                return "Terbit";
            case 3:
                return "Dhuhur";
            case 4:
                return "Ashar";
            case 5:
                return "Maghrib";
            case 6:
                return "Isya";
            default:
                return "";
        }
    }

    public static Long convertTimeStringToMillis(String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(timeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String getCurrentDateTime(boolean isNow) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = new Date();
        if (isNow) {
            return dateFormat.format(currentDate);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.set(Calendar.DAY_OF_MONTH, 28);
            calendar.add(Calendar.MONTH, 1);
            return dateFormat.format(calendar.getTime());
        }
    }

    public static boolean isIsya(String isyaTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        String currentTime = sdf.format(date);

        LocalTime now = LocalTime.parse(currentTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime isya = LocalTime.parse(isyaTime, DateTimeFormatter.ofPattern("HH:mm"));

        return now.isAfter(isya);
    }

    public static ArrayList<String> getPrayerList(PrayerEntity prayers) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(prayers.getImsak());
        arrayList.add(prayers.getFajr());
        arrayList.add(prayers.getSunrise());
        arrayList.add(prayers.getDhuhr());
        arrayList.add(prayers.getAsr());
        arrayList.add(prayers.getMaghrib());
        arrayList.add(prayers.getIsha());
        return arrayList;
    }

    public static String getStringBetweenCommas(String input, int startComma, int endComma) {
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

    public static void setAlarm(Context context, PrayerEntity prayerEntity) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Intent dan PendingIntent untuk Imsak
        Intent imsakIntent = new Intent(context, ImsakAlarmManager.class);
        PendingIntent imsakPendingIntent = PendingIntent.getBroadcast(context, 0, imsakIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getImsak()), imsakPendingIntent);

        // Intent dan PendingIntent untuk Subuh
        Intent subuhIntent = new Intent(context, SubuhAlarmManager.class);
        PendingIntent subuhPendingIntent = PendingIntent.getBroadcast(context, 1, subuhIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getFajr()), subuhPendingIntent);

        // Intent dan PendingIntent untuk Terbit
        Intent terbitIntent = new Intent(context, TerbitAlarmManager.class);
        PendingIntent terbitPendingIntent = PendingIntent.getBroadcast(context, 2, terbitIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getSunrise()), terbitPendingIntent);

        // Intent dan PendingIntent untuk Dhuhur
        Intent dhuhurIntent = new Intent(context, DhuhurAlarmManager.class);
        PendingIntent dhuhurPendingIntent = PendingIntent.getBroadcast(context, 3, dhuhurIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getDhuhr()), dhuhurPendingIntent);

        // Intent dan PendingIntent untuk Asr
        Intent asrIntent = new Intent(context, AsrAlarmManager.class);
        PendingIntent asrPendingIntent = PendingIntent.getBroadcast(context, 4, asrIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getAsr()), asrPendingIntent);

        // Intent dan PendingIntent untuk Maghrib
        Intent maghribIntent = new Intent(context, MaghribAlarmManager.class);
        PendingIntent maghribPendingIntent = PendingIntent.getBroadcast(context, 5, maghribIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getMaghrib()), maghribPendingIntent);

        // Intent dan PendingIntent untuk Isya
        Intent isyaIntent = new Intent(context, IsyaAlarmManager.class);
        PendingIntent isyaPendingIntent = PendingIntent.getBroadcast(context, 6, isyaIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getIsha()), isyaPendingIntent);
    }

    private static long getTimeMillis(String time) {
        int jam = Integer.parseInt(time.substring(0,2));
        int menit = Integer.parseInt(time.substring(3,5));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, jam);
        calendar.set(Calendar.MINUTE, menit);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }
}
