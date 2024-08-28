package com.showcase.weathertoday.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.showcase.weathertoday.R;
import com.showcase.weathertoday.controller.CurrentLocationController;
import com.showcase.weathertoday.controller.LocationController;
import com.showcase.weathertoday.controller.WeatherController;
import com.showcase.weathertoday.controller.WeatherPerHourAdapter;
import com.showcase.weathertoday.database.DatabaseHelper;
import com.showcase.weathertoday.model.LocationModel;
import com.showcase.weathertoday.utils.GlideCustomizado;
import com.showcase.weathertoday.utils.LanguageHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtViewCurrentLocation, txtViewCurrentTemperature, txtViewTemperatureMaxMin,
            txtViewCurrentDescription, txtViewWindSpeed, txtViewHumidity;
    private ImageView imgViewCurrentClimate;
    private ImageButton imgBtnManagement, imgBtnSwap, imgBtnDetectCurrentLocation;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private WeatherController weatherController;
    private boolean jaExisteLocal = false;
    private CurrentLocationController currentLocationController;
    private LocationController locationController;
    private WeatherPerHourAdapter weatherAdapter;
    private boolean preview = false;
    private LocationModel locationModelPreview;
    private boolean trocarLocal = false;
    private boolean detectarLocalAtual = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarComponentes();
    }

    @Override
    protected void onStart() {
        super.onStart();

        weatherController = new WeatherController();
        locationController = new LocationController(this);
        databaseHelper = new DatabaseHelper(this);
        currentLocationController = new CurrentLocationController(this);

        jaExisteLocal = databaseHelper.doesTableExist(DatabaseHelper.TABLE_COORDINATES);

        clickListeners();
        configRecyclerView();

        Bundle dados = getIntent().getExtras();

        if (dados != null && dados.containsKey("location")) {
            preview = true;
            locationModelPreview = (LocationModel) dados.getSerializable("location");
            imgBtnManagement.setVisibility(View.INVISIBLE);
            imgBtnSwap.setVisibility(View.VISIBLE);
        }

        if (jaExisteLocal) {
            recuperarLocal();
            recuperarDadosSQLite();
        } else {
            verificarPermissoes();
        }
    }

    private void clickListeners(){
        imgBtnDetectCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBtnSwap.setVisibility(View.INVISIBLE);
                detectarLocalAtual = true;
                verificarPermissoes();
            }
        });

        imgBtnManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GerenciarLocaisActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        imgBtnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trocarLocal = true;
                recuperarLocal();
                imgBtnSwap.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void configRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void verificarPermissoes() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permissão não concedida
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissão já concedida
            recuperarLocal();
        }
    }

    private void recuperarLocal(){
        if (detectarLocalAtual) {
            imgBtnDetectCurrentLocation.setVisibility(View.INVISIBLE);
        }

        if (preview && !detectarLocalAtual) {
            recuperarPrevisaoAtual(locationModelPreview.getLatitude(), locationModelPreview.getLongitude(), locationModelPreview.getNeighborhood());
        } else if (jaExisteLocal && !detectarLocalAtual) {
            LocationModel locationModel = locationController.getLocation(1);
            recuperarPrevisaoAtual(locationModel.getLatitude(), locationModel.getLongitude(), locationModel.getNeighborhood());
        } else {
            currentLocationController.recuperarLocalAtual(new CurrentLocationController.LocationCallback() {
                @Override
                public void onSuccess(LocationModel locationModelPrimary) {
                    recuperarPrevisaoAtual(locationModelPrimary.getLatitude(), locationModelPrimary.getLongitude(), locationModelPrimary.getNeighborhood());
                }

                @Override
                public void onFailure(Exception e) {
                    String msg = String.format("%s%s %s", getString(R.string.failed_to_retrieve_location),":",e.getMessage());
                    txtViewCurrentLocation.setText(msg);
                }
            });
        }
    }

    private void recuperarPrevisaoAtual(double latitude, double longitude, String neighborhood) {
        weatherController.recuperarDadosMeteorologicos(latitude, longitude, getApplicationContext(), LanguageHelper.recuperarLang(), new WeatherController.WeatherCallback() {
            @Override
            public void onSuccess(LocationModel locationModel) {
                locationModel.setNeighborhood(neighborhood);
                if (!jaExisteLocal && !preview) {
                    locationController.addLocation(locationModel.getLatitude(), locationModel.getLongitude(), neighborhood);
                } else if (jaExisteLocal && trocarLocal || detectarLocalAtual) {
                    if (neighborhood == null || neighborhood.isEmpty()) {
                        String currentLocation = String.format("%s%s %s%s %s", locationModelPreview.getName(), ",", locationModelPreview.getState(), ",", locationModelPreview.getCountry());
                        locationModel.setNeighborhood(currentLocation);
                    }
                    locationController.updateLocation(new LocationModel(1, locationModel.getLatitude(), locationModel.getLongitude(), locationModel.getNeighborhood()));
                    trocarLocal = false;
                    detectarLocalAtual = false;
                }
                exibirDados(locationModel);
                recuperar24HorasDePrevisao(locationModel.getLatitude(), locationModel.getLongitude());
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void recuperar24HorasDePrevisao(double latitude, double longitude){
        weatherController.previsaoDe24Horas(latitude, longitude, getApplicationContext(), LanguageHelper.recuperarLang(), new WeatherController.WeatherHoursCallback() {
            @Override
            public void onSuccess(List<LocationModel> listForecast) {
                weatherAdapter = new WeatherPerHourAdapter(listForecast, getApplicationContext());
                recyclerView.setAdapter(weatherAdapter);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void exibirDados(LocationModel locationModel) {
        GlideCustomizado.loadDrawableImage(getApplicationContext(), locationModel.getIconUrl(), imgViewCurrentClimate, android.R.color.transparent);
        txtViewCurrentLocation.setText(locationModel.getNeighborhood());
        String tempMaxMin = String.format("%s%s%s%s%s", locationModel.getTemperatureMax(), "°", "/", locationModel.getTemperatureMin(), "°");
        txtViewCurrentTemperature.setText(String.format("%s °C", locationModel.getTemperature()));
        txtViewTemperatureMaxMin.setText(tempMaxMin);
        txtViewCurrentDescription.setText(locationModel.getDescription());
        String windSpeed = String.format("%s %s", locationModel.getWindSpeed(), "m/s");
        txtViewWindSpeed.setText(windSpeed);
        String humidity = String.format("%s%s", locationModel.getHumidity(), "%");
        txtViewHumidity.setText(humidity);
    }

    private void recuperarDadosSQLite() {

        LocationModel locationModel = locationController.getLocation(1);

        Log.d("LocationDAO", "Lat: " + locationModel.getLatitude());
        Log.d("LocationDAO", "Long: " + locationModel.getLongitude());
        Log.d("LocationDAO", "Neighborhood: " + locationModel.getNeighborhood());

        List<LocationModel> listaRecuperada = locationController.getAllLocations();
        for (LocationModel locations : listaRecuperada) {
            Log.d("SQLITEDADOS", "Lat: " + locations.getLatitude());
            Log.d("SQLITEDADOS", "Long: " + locations.getLongitude());
            Log.d("SQLITEDADOS", "Neighborhood: " + locations.getNeighborhood());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                recuperarLocal();
            } else {
                // Permissão negada
                Toast.makeText(MainActivity.this, getString(R.string.notice_about_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void inicializarComponentes() {
        txtViewCurrentLocation = findViewById(R.id.txtViewCurrentLocation);
        txtViewCurrentTemperature = findViewById(R.id.txtViewCurrentTemperature);
        txtViewTemperatureMaxMin = findViewById(R.id.txtViewTemperatureMaxMin);
        txtViewCurrentDescription = findViewById(R.id.txtViewCurrentDescription);
        txtViewWindSpeed = findViewById(R.id.txtViewWindSpeed);
        txtViewHumidity = findViewById(R.id.txtViewHumidity);
        imgViewCurrentClimate = findViewById(R.id.imgViewCurrentClimate);
        imgBtnManagement = findViewById(R.id.imgBtnManagement);
        imgBtnSwap = findViewById(R.id.imgBtnSwap);
        imgBtnDetectCurrentLocation = findViewById(R.id.imgBtnDetectCurrentLocation);
        recyclerView = findViewById(R.id.recyclerView);
    }
}