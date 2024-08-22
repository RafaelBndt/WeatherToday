package com.showcase.weathertoday.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "coordinates.db";
    public static final String TABLE_COORDINATES = "coordinates";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_NEIGHBORHOOD = "neighborhood";
    public static final String COLUMN_ID = "id";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String TABLE_CREATE = String.format(
            "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s REAL, %s REAL, %s VARCHAR);",
            TABLE_COORDINATES, COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_NEIGHBORHOOD);

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDINATES);
       onCreate(db);
    }

    public boolean doesTableExist(String tableName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count > 0;
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}
