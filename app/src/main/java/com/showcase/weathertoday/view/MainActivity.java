package com.showcase.weathertoday.view;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.showcase.weathertoday.R;

public class MainActivity extends AppCompatActivity {

    private TextView txtViewCurrentLocation, txtViewCurrentTemperature, txtViewTemperatureMaxMin,
            txtViewCurrentDescription, txtViewWindSpeed, txtViewHumidity;
    private ImageView imgViewCurrentClimate;
    private ImageButton imgBtnManagement, imgBtnSwap, imgBtnDetectCurrentLocation;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarComponentes();
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