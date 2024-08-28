package com.showcase.weathertoday.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.showcase.weathertoday.database.DatabaseHelper;
import com.showcase.weathertoday.model.LocationModel;

import java.util.ArrayList;
import java.util.List;

public class LocationDAOImpl implements LocationDAO {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private static final String TAG = "LocationDAO";

    public LocationDAOImpl(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    private void close() {
        dbHelper.close();
    }

    @Override
    public void addLocation(LocationModel locationModel) {
        open();
        Cursor cursor = null;

        try {
            cursor = database.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_COORDINATES, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);

            if (count > 0) {
                Log.w(TAG, "Tentativa de inserir um novo registro quando já existe um.");
                return;
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_LATITUDE, locationModel.getLatitude());
            values.put(DatabaseHelper.COLUMN_LONGITUDE, locationModel.getLongitude());
            values.put(DatabaseHelper.COLUMN_NEIGHBORHOOD, locationModel.getNeighborhood());

            long result = database.insert(DatabaseHelper.TABLE_COORDINATES, null, values);
            if (result == -1) {
                Log.e(TAG, "Falha ao inserir o registro.");
            } else {
                Log.i(TAG, "Registro inserido com sucesso.");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
    }

    @Override
    public LocationModel getLocation(int id) {
        open();
        Cursor cursor = null;
        LocationModel locationModel = null;

        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_COORDINATES,
                    new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_LATITUDE, DatabaseHelper.COLUMN_LONGITUDE, DatabaseHelper.COLUMN_NEIGHBORHOOD},
                    null,
                    null,
                    null,
                    null,
                    null,
                    "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int latitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE);
                int neighborhoodIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NEIGHBORHOOD);

                int recordId = cursor.getInt(idIndex);
                double latitude = cursor.getDouble(latitudeIndex);
                double longitude = cursor.getDouble(longitudeIndex);
                String neighborhood = cursor.getString(neighborhoodIndex);

                locationModel = new LocationModel(recordId, latitude, longitude, neighborhood);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return locationModel;
    }

    @Override
    public List<LocationModel> getAllLocations() {
        open();
        List<LocationModel> locationList = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_COORDINATES,
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_LATITUDE, DatabaseHelper.COLUMN_LONGITUDE, DatabaseHelper.COLUMN_NEIGHBORHOOD},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                LocationModel locationModel = new LocationModel();
                locationModel.setId(cursor.getInt(0));
                locationModel.setLatitude(cursor.getDouble(1));
                locationModel.setLongitude(cursor.getDouble(2));
                locationList.add(locationModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return locationList;
    }

    @Override
    public void updateLocation(LocationModel locationModel) {
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LATITUDE, locationModel.getLatitude());
        values.put(DatabaseHelper.COLUMN_LONGITUDE, locationModel.getLongitude());
        values.put(DatabaseHelper.COLUMN_NEIGHBORHOOD, locationModel.getNeighborhood());
        int rowsAffected = database.update(DatabaseHelper.TABLE_COORDINATES,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(locationModel.getId())});

        if (rowsAffected > 0) {
            Log.i(TAG, "Registro atualizado com sucesso.");
        } else {
            Log.e(TAG, "Falha ao atualizar o registro.");
        }
        close();
    }

    @Override
    public void deleteLocation(int id) {
        open();
        int rowsAffected = database.delete(DatabaseHelper.TABLE_COORDINATES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});

        if (rowsAffected > 0) {
            Log.i(TAG, "Registro excluído com sucesso.");
        } else {
            Log.e(TAG, "Falha ao excluir o registro.");
        }

        close();
    }

    @Override
    public void deleteAllRecords() {
        open();
        database.delete(DatabaseHelper.TABLE_COORDINATES, null, null);
        close();
    }
}
