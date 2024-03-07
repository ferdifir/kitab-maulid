package com.sherdle.webtoapp.service.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PrayerDao {
    @Query("SELECT * FROM prayers WHERE date = :date")
    List<PrayerEntity> getPrayersByDate(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPrayer(List<PrayerEntity> prayers);

    @Query("DELETE FROM prayers")
    void deleteAllPrayers();
}

