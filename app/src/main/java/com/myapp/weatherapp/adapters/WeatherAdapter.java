package com.myapp.weatherapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.weatherapp.R;
import com.myapp.weatherapp.models.ForecastItem;
import com.myapp.weatherapp.utils.WeatherIconMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {
    private final List<ForecastItem> forecastItems;
    private final Context context;
    private final SimpleDateFormat inputFormat;
    private final SimpleDateFormat dayOutputFormat;
    private final SimpleDateFormat dateOutputFormat;

    public WeatherAdapter(List<ForecastItem> forecastItems, Context context) {
        if (forecastItems == null) {
            throw new IllegalArgumentException("ForecastItems list cannot be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        this.forecastItems = forecastItems;
        this.context = context;
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.dayOutputFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        this.dateOutputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ForecastItem forecastItem = forecastItems.get(position);

        // Set the forecast data to the views
        String dayText = formatDayOfWeek(forecastItem.getDate()) + " - " + formatDate(forecastItem.getDate());
        holder.dateTextView.setText(dayText);
        holder.tempHighTextView.setText(context.getString(R.string.forecast_temp_high, forecastItem.getTempMax()));
        holder.tempLowTextView.setText(context.getString(R.string.forecast_temp_low, forecastItem.getTempMin()));
        holder.precipitationTextView.setText(context.getString(R.string.forecast_precipitation, forecastItem.getPrecipitation()));

        String conditionCode = forecastItem.getWeatherCode();
        int iconResource = WeatherIconMapper.getWeatherIconResource(conditionCode);
        holder.weatherIcon.setImageResource(iconResource);

    }

    @Override
    public int getItemCount() {
        return forecastItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView tempHighTextView;
        TextView tempLowTextView;
        TextView precipitationTextView;
        ImageView weatherIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            tempHighTextView = itemView.findViewById(R.id.tempHighTextView);
            tempLowTextView = itemView.findViewById(R.id.tempLowTextView);
            precipitationTextView = itemView.findViewById(R.id.precipitationTextView);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }

    private String formatDate(String date) {
        try {
            Date inputDate = inputFormat.parse(date);
            if (inputDate != null) {
                return dateOutputFormat.format(inputDate);
            }
        } catch (ParseException e) {
            Log.e("WeatherAdapter", "Error parsing date: " + date, e);
        }
        return date;
    }

    private String formatDayOfWeek(String date) {
        try {
            Date inputDate = inputFormat.parse(date);
            if (inputDate != null) {
                return dayOutputFormat.format(inputDate);
            }
        } catch (ParseException e) {
            Log.e("WeatherAdapter", "Error parsing date: " + date, e);
        }
        return "";
    }
}


