package com.showcase.weathertoday.controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.showcase.weathertoday.BuildConfig;
import com.showcase.weathertoday.R;
import com.showcase.weathertoday.model.LocationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WeatherFindController {
    private static final String API_KEY_OPEN_WEATHER = BuildConfig.API_KEY_OPEN_WEATHER;
    private static final String API_KEY_GEOCODING = BuildConfig.API_KEY_GEOCODING;
    private static final String urlOpenWeather = "https://api.openweathermap.org/data/2.5/find?q=";
    private static final String urlOpenCageData = "https://api.opencagedata.com/geocode/v1/json?q=";
    private RequestQueue requestQueue;

    public WeatherFindController(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void procurarLocais(String query, Context context, String lang, final LocationsSearchCallback callback) {
        String tempUrl = urlOpenWeather + query + "&type=like&sort=population&cnt=30&appid=" + API_KEY_OPEN_WEATHER + "&units=metric" + "&lang=" + lang;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, tempUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray list = response.getJSONArray("list");

                            if (list.length() == 0) {
                                callback.onNoResultsFound();
                                return;
                            }

                            final Map<String, LocationModel> locationMap = new HashMap<>();
                            final int totalLocations = list.length();
                            final AtomicInteger completedRequests = new AtomicInteger(0);

                            for (int i = 0; i < list.length(); i++) {
                                final JSONObject locationObject = list.getJSONObject(i);
                                final LocationModel locationModel = new LocationModel();
                                locationModel.setName(locationObject.getString("name"));
                                locationModel.setCountry(locationObject.getJSONObject("sys").getString("country"));

                                double lat = locationObject.getJSONObject("coord").getDouble("lat");
                                double lon = locationObject.getJSONObject("coord").getDouble("lon");
                                locationModel.setLatitude(lat);
                                locationModel.setLongitude(lon);

                                JSONObject main = locationObject.getJSONObject("main");
                                locationModel.setTemperature(String.valueOf(main.getDouble("temp")));
                                locationModel.setTemperatureMin(String.valueOf(main.getDouble("temp_min")));
                                locationModel.setTemperatureMax(String.valueOf(main.getDouble("temp_max")));

                                JSONArray weatherArray = locationObject.getJSONArray("weather");
                                JSONObject weather = weatherArray.getJSONObject(0);
                                locationModel.setDescription(weather.getString("description"));

                                String iconName = weather.getString("icon");
                                String iconUrl = String.format("%s%s%s", "https://openweathermap.org/img/wn/",iconName,"@4x.png");

                                locationModel.setIconUrl(iconUrl);

                                recuperarEstadoPelasCoordenadas(lat, lon, locationModel, new LocationsSearchCallback() {
                                    @Override
                                    public void onSuccess(List<LocationModel> resultList) {
                                        String key = locationModel.getName() + "|" + locationModel.getCountry() + "|" + locationModel.getState();
                                        locationMap.put(key, locationModel);

                                        if (completedRequests.incrementAndGet() == totalLocations) {
                                            if (locationMap.isEmpty()) {
                                                callback.onNoResultsFound();
                                            } else {
                                                callback.onSuccess(new ArrayList<>(locationMap.values()));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        if (completedRequests.incrementAndGet() == totalLocations) {
                                            callback.onFailure(new Exception(context.getString(R.string.failed_to_retrieve_states)));
                                        }
                                    }

                                    @Override
                                    public void onNoResultsFound() {
                                        callback.onNoResultsFound();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            callback.onFailure(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure(error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    private void recuperarEstadoPelasCoordenadas(double lat, double lon, final LocationModel locationModel, final LocationsSearchCallback callback) {
        String url = urlOpenCageData + lat + "+" + lon + "&key=" + API_KEY_GEOCODING;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONArray("results").getJSONObject(0);
                            String state = result.getJSONObject("components").optString("state", "N/A");
                            locationModel.setState(state);
                            callback.onSuccess(Collections.singletonList(locationModel));
                        } catch (JSONException e) {
                            callback.onFailure(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure(error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public interface LocationsSearchCallback {
        void onSuccess(List<LocationModel> locationModelList);

        void onFailure(Exception e);

        void onNoResultsFound();
    }
}
