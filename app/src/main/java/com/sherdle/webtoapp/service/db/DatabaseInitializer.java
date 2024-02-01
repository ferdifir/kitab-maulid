package com.sherdle.webtoapp.service.db;

import android.content.Context;

import androidx.room.Room;

public class DatabaseInitializer {
    private static AppDatabase database;

    public static AppDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app-database")
                    .build();
        }
        return database;
    }
}