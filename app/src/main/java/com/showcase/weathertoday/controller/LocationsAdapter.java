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

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder> {
    private List<LocationModel> locationModelList;
    private OnCityClickListener onCityClickListener;
    private Context context;

    public LocationsAdapter(Context context, List<LocationModel> locationModelList, OnCityClickListener onCityClickListener) {
        this.locationModelList = locationModelList;
        this.onCityClickListener = onCityClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public LocationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_locations, parent, false);
        return new LocationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsViewHolder holder, int position) {
        LocationModel locationModel = locationModelList.get(position);
        holder.bind(locationModel, context, onCityClickListener);
    }

    @Override
    public int getItemCount() {
        return locationModelList.size();
    }

    static class LocationsViewHolder extends RecyclerView.ViewHolder {
        private TextView txtViewLocationName, txtViewTempFind, txtViewDescFind;
        private ImageView imgViewClimateFind;


        public LocationsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewLocationName = itemView.findViewById(R.id.txtViewLocationName);
            imgViewClimateFind = itemView.findViewById(R.id.imgViewClimateFind);
            txtViewTempFind = itemView.findViewById(R.id.txtViewTempFind);
            txtViewDescFind = itemView.findViewById(R.id.txtViewDescFind);
        }

        public void bind(final LocationModel locationModel, Context context, final OnCityClickListener onCityClickListener) {
            GlideCustomizado.loadDrawableImage(context, locationModel.getIconUrl(), imgViewClimateFind, android.R.color.transparent);
            String info = String.format("%s%s%s%s%s", locationModel.getName(), ",", locationModel.getState(), ",", locationModel.getCountry());
            txtViewLocationName.setText(info);
            txtViewTempFind.setText(String.format("%s °", locationModel.getTemperature()));
            txtViewDescFind.setText(locationModel.getDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCityClickListener.onCityClick(locationModel);
                }
            });
        }
    }

    public interface OnCityClickListener {
        void onCityClick(LocationModel locationModel);
    }
}

