package com.sherdle.webtoapp.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "prayer_times_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "prayer_times";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_FAJR = "fajr";
    public static final String COLUMN_SUNRISE = "sunrise";
    public static final String COLUMN_DHUHR = "dhuhr";
    public static final String COLUMN_ASR = "asr";
    public static final String COLUMN_SUNSET = "sunset";
    public static final String COLUMN_MAGHRIB = "maghrib";
    public static final String COLUMN_ISHA = "isha";
    public static final String COLUMN_IMSAK = "imsak";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE  + " TEXT, " +
                    COLUMN_FAJR + " TEXT, " +
                    COLUMN_SUNRISE + " TEXT, " +
                    COLUMN_DHUHR + " TEXT, " +
                    COLUMN_ASR + " TEXT, " +
                    COLUMN_SUNSET + " TEXT, " +
                    COLUMN_MAGHRIB + " TEXT, " +
                    COLUMN_ISHA + " TEXT, " +
                    COLUMN_IMSAK + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertBulkData(ContentValues[] contentValuesArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            for (ContentValues contentValues : contentValuesArray) {
                db.insert(TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean isDataExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT COUNT(*) FROM" + TABLE_NAME;
            cursor = db.rawQuery(query, null);
            cursor.
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }
}