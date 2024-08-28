package com.showcase.weathertoday.controller;

import android.content.Context;

import com.showcase.weathertoday.dao.LocationDAO;
import com.showcase.weathertoday.dao.LocationDAOImpl;
import com.showcase.weathertoday.model.LocationModel;

import java.util.List;

public class LocationController {
    private LocationDAO locationDAO;

    public LocationController(Context context) {
        locationDAO = new LocationDAOImpl(context);
    }

    public void addLocation(double latitude, double longitude, String neighborhood) {
        LocationModel locationModel = new LocationModel(latitude, longitude, neighborhood);
        locationDAO.addLocation(locationModel);
    }

    public LocationModel getLocation(int id) {
        return locationDAO.getLocation(id);
    }

    public List<LocationModel> getAllLocations() {
        return locationDAO.getAllLocations();
    }

    public void updateLocation(LocationModel locationModel) {
        locationDAO.updateLocation(locationModel);
    }

    public void deleteLocation(int id) {
        locationDAO.deleteLocation(id);
    }

    public void deleteAllRecords() {
        locationDAO.deleteAllRecords();
    }
}
