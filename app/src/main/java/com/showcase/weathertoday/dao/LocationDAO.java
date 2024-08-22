package com.showcase.weathertoday.dao;

import com.showcase.weathertoday.model.LocationModel;

import java.util.List;

public interface LocationDAO {
    void addLocation(LocationModel locationModel);
    LocationModel getLocation(int id);
    List<LocationModel> getAllLocations();
    void updateLocation(LocationModel locationModel);
    void deleteLocation(int id);
    void deleteAllRecords();
}
