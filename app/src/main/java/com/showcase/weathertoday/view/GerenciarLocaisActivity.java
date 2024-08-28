package com.showcase.weathertoday.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.showcase.weathertoday.R;
import com.showcase.weathertoday.controller.LocationsAdapter;
import com.showcase.weathertoday.controller.WeatherFindController;
import com.showcase.weathertoday.model.LocationModel;
import com.showcase.weathertoday.utils.LanguageHelper;

import java.util.List;

public class GerenciarLocaisActivity extends AppCompatActivity {

    private LocationsAdapter cityAdapter;
    private WeatherFindController weatherFindController;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_locais);

        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(cityAdapter);

        weatherFindController = new WeatherFindController(getApplicationContext());
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String dado = query.trim();
                if (!dado.isEmpty()) {
                    procurarLocais(dado);
                    swipeRefreshLayout.setRefreshing(true);
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_a_valid_location), Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void procurarLocais(String query) {
        weatherFindController.procurarLocais(query, getApplicationContext(), LanguageHelper.recuperarLang(), new WeatherFindController.LocationsSearchCallback() {
            @Override
            public void onSuccess(final List<LocationModel> locationModelList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cityAdapter = new LocationsAdapter(getApplicationContext(), locationModelList, new LocationsAdapter.OnCityClickListener() {
                            @Override
                            public void onCityClick(LocationModel locationModel) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("location", new LocationModel(locationModel.getLatitude(), locationModel.getLongitude(), locationModel.getName(), locationModel.getState(), locationModel.getCountry()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                        recyclerView.setAdapter(cityAdapter);
                        cityAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                swipeRefreshLayout.setRefreshing(false);
                e.printStackTrace();
            }

            @Override
            public void onNoResultsFound() {
                Toast.makeText(getApplicationContext(), getString(R.string.no_location_with_that_name), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}