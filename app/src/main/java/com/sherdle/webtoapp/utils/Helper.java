package com.sherdle.webtoapp.utils;

import com.sherdle.webtoapp.service.api.response.schedule.Timings;
import com.sherdle.webtoapp.service.db.PrayerEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Helper {

    public static ArrayList<Long> getPrayerScheduleList(PrayerEntity timings) {
        ArrayList<Long> list = new ArrayList<>();
        list.add(convertTimeStringToMillis(timings.getImsak()));
        list.add(convertTimeStringToMillis(timings.getFajr()));
        list.add(convertTimeStringToMillis(timings.getSunrise()));
        list.add(convertTimeStringToMillis(timings.getDhuhr()));
        list.add(convertTimeStringToMillis(timings.getAsr()));
        list.add(convertTimeStringToMillis(timings.getMaghrib()));
        list.add(convertTimeStringToMillis(timings.getIsha()));
        return list;
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

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static int compareTimeStrings(String time1, String time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
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

    public static long getSelisihWaktuSholatTerdekat(ArrayList<String> jadwalSholat) {
        long selisihTerdekat = Long.MAX_VALUE;
        long waktuSekarang = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        for (String waktuSholat : jadwalSholat) {
            try {
                Date waktuSholatDate = sdf.parse(waktuSholat);
                long waktuSholatMillis = waktuSholatDate.getTime();

                if (waktuSholatMillis > waktuSekarang && waktuSholatMillis - waktuSekarang < selisihTerdekat) {
                    selisihTerdekat = waktuSholatMillis - waktuSekarang;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return selisihTerdekat;
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
}
