package com.showcase.weathertoday.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.showcase.weathertoday.R;
import com.showcase.weathertoday.model.LocationModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CurrentLocationController {
    private final Geocoder geocoder;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private Context context;

    public CurrentLocationController(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        geocoder = new Geocoder(context, Locale.getDefault());
        this.context = context;
    }

    public interface LocationCallback {
        void onSuccess(LocationModel locationModel);

        void onFailure(Exception e);
    }

    public void recuperarLocalAtual(final LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                LocationModel locationModel = new LocationModel();
                                locationModel.setCountry(address.getCountryName());
                                String locality = address.getLocality();

                                if (locality == null) {
                                    locality = address.getSubAdminArea();
                                }

                                locationModel.setName(locality);
                                locationModel.setNeighborhood(address.getSubLocality());

                                locationModel.setLatitude(address.getLatitude());
                                locationModel.setLongitude(address.getLongitude());

                                callback.onSuccess(locationModel);
                            }
                        } catch (IOException e) {
                            callback.onFailure(e);
                        }
                    } else {
                        callback.onFailure(new Exception(context.getString(R.string.invalid_location)));
                    }
                });
    }
}
