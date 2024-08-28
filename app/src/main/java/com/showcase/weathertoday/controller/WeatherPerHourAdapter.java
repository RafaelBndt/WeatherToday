package com.showcase.weathertoday.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.showcase.weathertoday.R;
import com.showcase.weathertoday.model.LocationModel;
import com.showcase.weathertoday.utils.GlideCustomizado;

import java.util.List;

public class WeatherPerHourAdapter extends RecyclerView.Adapter<WeatherPerHourAdapter.WeatherPerHourViewHolder> {
    private List<LocationModel> weatherForecasts;
    private Context context;

    public WeatherPerHourAdapter(List<LocationModel> weatherForecasts, Context context) {
        this.weatherForecasts = weatherForecasts;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherPerHourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        return new WeatherPerHourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherPerHourViewHolder holder, int position) {
        LocationModel locationModel = weatherForecasts.get(position);
        holder.txtViewDay.setText(locationModel.getTime());
        holder.txtViewDescPerHour.setText(locationModel.getDescription());
        String temp = String.format("%s %s", locationModel.getTemperature(),"°C");
        holder.txtViewTempPerHour.setText(temp);
        GlideCustomizado.loadDrawableImage(context, locationModel.getIconUrl(), holder.imgViewWeatherPerHour, android.R.color.transparent);
    }

    @Override
    public int getItemCount() {
        return weatherForecasts.size();
    }

    static class WeatherPerHourViewHolder extends RecyclerView.ViewHolder {
        ImageView imgViewWeatherPerHour;
        TextView txtViewDay;
        TextView txtViewDescPerHour;
        TextView txtViewTempPerHour;

        WeatherPerHourViewHolder(View itemView) {
            super(itemView);
            imgViewWeatherPerHour = itemView.findViewById(R.id.imgViewWeatherPerHour);
            txtViewDay = itemView.findViewById(R.id.txtViewDay);
            txtViewDescPerHour = itemView.findViewById(R.id.txtViewDescPerHour);
            txtViewTempPerHour = itemView.findViewById(R.id.txtViewTempPerHour);
        }
    }
}
