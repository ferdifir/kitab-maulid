package com.sherdle.webtoapp.service.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sherdle.webtoapp.service.db.entity.PrayerSchedule;

import java.util.List;

@Dao
public interface PrayerDao {
    @Insert
    void insertPrayerSchedule(List<PrayerSchedule> prayerSchedules);

    @Query("SELECT * FROM prayers WHERE date = :currentDate")
    List<PrayerSchedule> getCurrentSchedule(String currentDate);
}