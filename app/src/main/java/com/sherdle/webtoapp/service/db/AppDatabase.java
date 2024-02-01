package com.sherdle.webtoapp.service.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.sherdle.webtoapp.service.db.entity.PrayerSchedule;

@Database(entities = {PrayerSchedule.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PrayerDao prayerDao();
}

