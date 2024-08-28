package com.showcase.weathertoday.controller;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.showcase.weathertoday.BuildConfig;
import com.showcase.weathertoday.model.LocationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherController {
    private static final String API_KEY_WEATHER = BuildConfig.API_KEY_WEATHER;
    private static final String urlWeather = "https://api.weatherapi.com/v1/forecast.json?key=";

    public interface WeatherCallback {
        void onSuccess(LocationModel locationModel);

        void onFailure(Exception e);
    }

    public interface WeatherHoursCallback {
        void onSuccess(List<LocationModel> listForecast);

        void onFailure(Exception e);
    }

    public void recuperarDadosMeteorologicos(double latitude, double longitude, Context context, String lang, final WeatherCallback callback){
        String url = urlWeather + API_KEY_WEATHER + "&q=" + latitude + "," + longitude + "&lang=" + lang;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    String decodedString = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(decodedString);
                    JSONObject currentWeather = jsonObject.getJSONObject("current");
                    JSONObject condition = currentWeather.getJSONObject("condition");
                    JSONObject location = jsonObject.getJSONObject("location");

                    double latitude = location.getDouble("lat");
                    double longitude = location.getDouble("lon");
                    String description = condition.getString("text");
                    String iconUrl = String.format("%s%s","https:", condition.getString("icon"));
                    String temperature = String.valueOf((int) Math.round(currentWeather.getDouble("temp_c")));
                    String humidity = String.valueOf(currentWeather.getInt("humidity"));
                    double windMph = currentWeather.getDouble("wind_mph");
                    double mts = windMph * 0.44704;
                    DecimalFormat df = new DecimalFormat("#.##");
                    String windSpeed =  df.format(mts);

                    JSONArray forecastArray = jsonObject.getJSONObject("forecast").getJSONArray("forecastday");
                    JSONObject day = forecastArray.getJSONObject(0).getJSONObject("day");

                    String temperatureMax = String.valueOf((int) Math.round(day.getDouble("maxtemp_c")));
                    String temperatureMin = String.valueOf((int) Math.round(day.getDouble("mintemp_c")));

                    LocationModel locationModel = new LocationModel();
                    locationModel.setIconUrl(iconUrl);
                    locationModel.setDescription(description);
                    locationModel.setTemperature(temperature);
                    locationModel.setTemperatureMax(temperatureMax);
                    locationModel.setTemperatureMin(temperatureMin);
                    locationModel.setHumidity(humidity);
                    locationModel.setWindSpeed(windSpeed);
                    locationModel.setLatitude(latitude);
                    locationModel.setLongitude(longitude);
                    callback.onSuccess(locationModel);
                }catch (JSONException e){
                    callback.onFailure(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void previsaoDe24Horas(double latitude, double longitude, Context context, String lang, final WeatherHoursCallback callback) {
        String url = urlWeather + API_KEY_WEATHER + "&q=" + latitude + "," + longitude + "&hours=24" + "&lang=" + lang;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<LocationModel> weatherList = new ArrayList<>();
                            String decodedString = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                            JSONObject jsonObject = new JSONObject(decodedString);

                            JSONArray forecastArray = jsonObject.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour");

                            long currentTime = System.currentTimeMillis() / 1000L;

                            for (int i = 0; i < forecastArray.length(); i++) {
                                JSONObject hourObject = forecastArray.getJSONObject(i);
                                long forecastTime = hourObject.getLong("time_epoch");

                                if (forecastTime > currentTime) {
                                    String time = hourObject.getString("time");
                                    String temp = hourObject.getString("temp_c");

                                    String condition = hourObject.getJSONObject("condition").getString("text");
                                    String iconUrlOne = hourObject.getJSONObject("condition").getString("icon");
                                    String iconUrl = String.format("%s%s","https://", iconUrlOne);

                                    weatherList.add(new LocationModel(time, condition, temp, iconUrl));

                                    if (weatherList.size() >= 24) {
                                        break;
                                    }
                                }
                            }
                            callback.onSuccess(weatherList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

}
