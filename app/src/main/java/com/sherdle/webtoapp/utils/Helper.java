package com.sherdle.webtoapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.sherdle.webtoapp.Config;
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
import java.time.temporal.ChronoUnit;
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

    public static void setAlarm(Context context, PrayerEntity prayerEntity, int prayerIndex) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent;
        PendingIntent pendingIntent;

        switch (prayerIndex) {
            case 0:
                intent = new Intent(context, ImsakAlarmManager.class);
                pendingIntent = PendingIntent.getBroadcast(context, Config.IMSAK_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getImsak(), false), pendingIntent);
                break;
            case 1:
                intent = new Intent(context, SubuhAlarmManager.class);
                pendingIntent = PendingIntent.getBroadcast(context, Config.SUBUH_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getFajr(),false), pendingIntent);
                break;
            case 2:
                intent = new Intent(context, TerbitAlarmManager.class);
                pendingIntent = PendingIntent.getBroadcast(context, Config.TERBIT_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getSunrise(),false), pendingIntent);
                break;
            case 3:
                intent = new Intent(context, DhuhurAlarmManager.class);
                pendingIntent = PendingIntent.getBroadcast(context, Config.DHUHUR_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getDhuhr(),false), pendingIntent);
                break;
            case 4:
                intent = new Intent(context, AsrAlarmManager.class);
                pendingIntent = PendingIntent.getBroadcast(context, Config.ASHAR_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getAsr(),false), pendingIntent);
                break;
            case 5:
                intent = new Intent(context, MaghribAlarmManager.class);
                pendingIntent = PendingIntent.getBroadcast(context, Config.MAGHRIB_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getMaghrib(),false), pendingIntent);
                break;
            case 6:
                intent = new Intent(context, IsyaAlarmManager.class);
                pendingIntent = PendingIntent.getBroadcast(context, Config.ISYA_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getTimeMillis(prayerEntity.getIsha(),false), pendingIntent);
                break;
            default:
                // Handle default case if needed
                break;
        }
    }

    public static long getTimeMillis(String time, boolean tomorrow) {
        int jam = Integer.parseInt(time.substring(0,2));
        int menit = Integer.parseInt(time.substring(3,5));
        Calendar calendar = Calendar.getInstance();
        if (tomorrow) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, jam);
        calendar.set(Calendar.MINUTE, menit);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getDelayNextPrayer(PrayerEntity todaySchedule, String imsakTomorrow) {
        boolean isAfterIsya = Helper.isIsya(todaySchedule.getIsha());
        LocalTime now = LocalTime.now();
        LocalTime midnight = LocalTime.of(23, 59);
        if (isAfterIsya) {
            long toImsak = LocalTime.MIDNIGHT.until(
                    LocalTime.of(
                            Integer.parseInt(imsakTomorrow.substring(0,2)),
                            Integer.parseInt(imsakTomorrow.substring(3))),
                    ChronoUnit.MILLIS
            );
            if (now.isBefore(midnight)) {
                long toMidnight = now.until(midnight, ChronoUnit.MILLIS);
                return toMidnight + toImsak;
            } else {
                return toImsak;
            }
        } else {
            List<String> prayerList = Helper.getPrayerList(todaySchedule);
            for (int i = 0; i < prayerList.size(); i++) {
                int hour = Integer.parseInt(prayerList.get(i).substring(0, 2));
                int minute = Integer.parseInt(prayerList.get(i).substring(3));
                LocalTime prayerSchedule = LocalTime.of(hour, minute);
                if (prayerSchedule.isAfter(now)) {
                    return now.until(prayerSchedule, ChronoUnit.MILLIS);
                }
            }
            return 0L;
        }
    }

    public static String getSoundUri(int index) {
        switch (index) {
            case 1:
                return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
            case 2:
                return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
            default:
                return "";
        }
    }
}
