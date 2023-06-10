package com.myapp.weatherapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.myapp.weatherapp.R;
import com.myapp.weatherapp.models.WeatherItem;
import com.myapp.weatherapp.utils.DateUtils;
import com.myapp.weatherapp.utils.WeatherIconMapper;
import com.myapp.weatherapp.viewmodels.LoadState;
import com.myapp.weatherapp.viewmodels.WeatherViewModel;

import org.json.JSONException;

import java.util.List;

public class TodayWeatherFragment extends Fragment {

    private TextView dayOfWeekTextView;
    private TextView dateTextView;
    private TextView tempHighTextView;
    private TextView tempLowTextView;
    private TextView precipitationTextView;
    private ImageView weatherIcon;
    private ProgressBar progressBar;
    private LinearLayout parentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_weather, container, false);
        setupViews(view);
        setupWeatherViewModel();
        return view;
    }

    private void setupViews(View view) {
        parentLayout = view.findViewById(R.id.parentLayout);
        progressBar = view.findViewById(R.id.progressBar);
        dayOfWeekTextView = view.findViewById(R.id.todayTextView);
        dateTextView = view.findViewById(R.id.todayDateTextView);
        tempHighTextView = view.findViewById(R.id.todayTempHighTextView);
        tempLowTextView = view.findViewById(R.id.todayTempLowTextView);
        precipitationTextView = view.findViewById(R.id.todayPrecipitationTextView);
        weatherIcon = view.findViewById(R.id.todayWeatherIcon);

        parentLayout.setVisibility(View.GONE);

    }

    private void setupWeatherViewModel() {
        WeatherViewModel weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        weatherViewModel.getWeatherItemsLiveData().observe(getViewLifecycleOwner(), this::handleTodayWeatherResponse);
        weatherViewModel.getState().observe(getViewLifecycleOwner(), this::handleLoadState);
    }

    private void handleTodayWeatherResponse(List<WeatherItem> weatherItems) {
        if (!weatherItems.isEmpty()) {
            WeatherItem todayWeather = weatherItems.get(0);
            dayOfWeekTextView.setText(DateUtils.formatDayOfWeek(todayWeather.getDate()));
            dateTextView.setText(DateUtils.formatDate(todayWeather.getDate()));
            tempHighTextView.setText(getString(R.string.forecast_temp_high, todayWeather.getTempMax()));
            tempLowTextView.setText(getString(R.string.forecast_temp_low, todayWeather.getTempMin()));
            precipitationTextView.setText(getString(R.string.forecast_precipitation, todayWeather.getPrecipitation()));

            String conditionCode = todayWeather.getWeatherCode();
            int iconResource = WeatherIconMapper.getWeatherIconResource(conditionCode);
            weatherIcon.setImageResource(iconResource);
        }
    }

    private void handleLoadState(LoadState state) {
        if (state instanceof LoadState.Loading) {
            showLoadingState();
        } else if (state instanceof LoadState.Error) {
            Throwable error = ((LoadState.Error) state).error;
            handleVolleyError(error);
            hideLoadingState();
        } else if (state instanceof LoadState.Success) {
            hideLoadingState();
            handleSuccessState();
        }
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingState() {
        progressBar.setVisibility(View.GONE);
    }

    private void handleSuccessState() {
        progressBar.setVisibility(View.GONE);
        parentLayout.setVisibility(View.VISIBLE);
    }

    private void handleVolleyError(Throwable error) {
        String errorMessage = getErrorMessage(error);
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private String getErrorMessage(Throwable error) {
        if (error instanceof VolleyError) {
            VolleyError volleyError = (VolleyError) error;
            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                return new String(volleyError.networkResponse.data);
            } else if (volleyError.getCause() instanceof JSONException) {
                return getString(R.string.json_parsing_error);
            } else if (volleyError instanceof TimeoutError) {
                return getString(R.string.connection_timed_out);
            } else if (volleyError instanceof NoConnectionError) {
                return getString(R.string.no_connection_to_server);
            } else if (volleyError instanceof ServerError) {
                return getString(R.string.server_error);
            }
        }

        return getString(R.string.unknown_error_occurred);
    }

}


