package com.sherdle.webtoapp.service.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "prayers")
public class PrayerSchedule {
    @NonNull
    @PrimaryKey
    private String date;

    private String sunset;
    private String asr;
    private String isha;
    private String fajr;
    private String dhuhr;
    private String maghrib;
    private String sunrise;
    private String midnight;
    private String imsak;

    public PrayerSchedule() {

    }

    public PrayerSchedule(String date, String sunset, String asr, String isha, String fajr,
                          String dhuhr, String maghrib, String sunrise, String midnight, String imsak) {
        this.date = date;
        this.sunset = sunset;
        this.asr = asr;
        this.isha = isha;
        this.fajr = fajr;
        this.dhuhr = dhuhr;
        this.maghrib = maghrib;
        this.sunrise = sunrise;
        this.midnight = midnight;
        this.imsak = imsak;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    public String getIsha() {
        return isha;
    }

    public void setIsha(String isha) {
        this.isha = isha;
    }

    public String getFajr() {
        return fajr;
    }

    public void setFajr(String fajr) {
        this.fajr = fajr;
    }

    public String getDhuhr() {
        return dhuhr;
    }

    public void setDhuhr(String dhuhr) {
        this.dhuhr = dhuhr;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(String maghrib) {
        this.maghrib = maghrib;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getMidnight() {
        return midnight;
    }

    public void setMidnight(String midnight) {
        this.midnight = midnight;
    }

    public String getImsak() {
        return imsak;
    }

    public void setImsak(String imsak) {
        this.imsak = imsak;
    }
}
