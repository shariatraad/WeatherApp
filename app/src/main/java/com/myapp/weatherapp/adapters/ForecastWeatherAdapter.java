package com.myapp.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.weatherapp.R;
import com.myapp.weatherapp.models.WeatherItem;
import com.myapp.weatherapp.utils.DateUtils;
import com.myapp.weatherapp.utils.WeatherIconMapper;

import java.util.List;

// Adapter for the RecyclerView in the forecast weather screen
public class ForecastWeatherAdapter extends RecyclerView.Adapter<ForecastWeatherAdapter.MyViewHolder> {
    private final List<WeatherItem> weatherItems;
    private final Context context;

    // Constructor for the ForecastWeatherAdapter.
    public ForecastWeatherAdapter(List<WeatherItem> weatherItems, Context context) {
        this.weatherItems = weatherItems;
        this.context = context;
    }

    @NonNull
    @Override
    // Inflate layout from XML and return the holder
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_weather_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    // Populate data into the item through holder
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WeatherItem weatherItem = weatherItems.get(position);

        holder.dayTextView.setText(DateUtils.formatDayOfWeekForecast(weatherItem.getDate(), weatherItem.getTimeZone()));
        holder.dateTextView.setText(DateUtils.formatDateForecast(weatherItem.getDate(), weatherItem.getTimeZone()));
        holder.tempHighTextView.setText(context.getString(R.string.forecast_temp_high, weatherItem.getTempMax()));
        holder.tempLowTextView.setText(context.getString(R.string.forecast_temp_low, weatherItem.getTempMin()));
        holder.precipitationTextView.setText(context.getString(R.string.forecast_precipitation, weatherItem.getPrecipitation()));

        String conditionCode = weatherItem.getWeatherCode();
        int iconResource = WeatherIconMapper.getWeatherIconResource(conditionCode);
        holder.weatherIcon.setImageResource(iconResource);
    }

    @Override
    // Indicate the number of items to display
    public int getItemCount() {
        return weatherItems.size();
    }

    // Define the view holder, holding the individual list items.
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        TextView dateTextView;
        TextView tempHighTextView;
        TextView tempLowTextView;
        TextView precipitationTextView;
        ImageView weatherIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            tempHighTextView = itemView.findViewById(R.id.tempHighTextView);
            tempLowTextView = itemView.findViewById(R.id.tempLowTextView);
            precipitationTextView = itemView.findViewById(R.id.precipitationTextView);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }
}
