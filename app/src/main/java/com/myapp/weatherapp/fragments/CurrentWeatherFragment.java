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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

public class CurrentWeatherFragment extends Fragment {

    private TextView dayOfWeekTextView;
    private TextView dateTextView;
    private TextView currentTempTextView;
    private TextView currentSunriseTextView;
    private TextView currentSunsetTextView;
    private ImageView weatherIcon;
    private ProgressBar progressBar;
    private LinearLayout parentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        setupViews(view);
        setupWeatherViewModel();
        return view;
    }

    private void setupViews(View view) {
        parentLayout = view.findViewById(R.id.parentLayout);
        progressBar = view.findViewById(R.id.progressBar);
        dayOfWeekTextView = view.findViewById(R.id.currentTextView);
        dateTextView = view.findViewById(R.id.currentDateTextView);
        currentTempTextView = view.findViewById(R.id.currentTempTextView);
        currentSunriseTextView = view.findViewById(R.id.currentSunriseTextView);
        currentSunsetTextView = view.findViewById(R.id.currentSunsetTextView);
        weatherIcon = view.findViewById(R.id.currentWeatherIcon);
        parentLayout.setVisibility(View.GONE);

    }


    private void setupWeatherViewModel() {
        WeatherViewModel weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        weatherViewModel.getCurrentWeatherLiveData().observe(getViewLifecycleOwner(), this::handleCurrentWeatherResponse);
        weatherViewModel.getState().observe(getViewLifecycleOwner(), this::handleLoadState);
        weatherViewModel.getChosenLocation().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newLocation) {
                weatherViewModel.getCurrentWeather(requireActivity());
            }
        });

    }


    private void handleCurrentWeatherResponse(WeatherItem currentWeather) {

        dayOfWeekTextView.setText(DateUtils.formatDayOfWeekCurrent(currentWeather.getDate(), currentWeather.getTimeZone()));
        dateTextView.setText(DateUtils.formatDateCurrent(currentWeather.getDate(), currentWeather.getTimeZone()));
        currentTempTextView.setText(getString(R.string.current_temp, currentWeather.getTempCurrent()));
        currentSunriseTextView.setText(DateUtils.convertUtcToLocal(currentWeather.getSunrise(), currentWeather.getTimeZone()));
        currentSunsetTextView.setText(DateUtils.convertUtcToLocal(currentWeather.getSunset(), currentWeather.getTimeZone()));
        String conditionCode = currentWeather.getWeatherCode();
        int iconResource = WeatherIconMapper.getWeatherIconResource(conditionCode);
        weatherIcon.setImageResource(iconResource);
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


