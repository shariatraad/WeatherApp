package com.myapp.weatherapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.myapp.weatherapp.R;
import com.myapp.weatherapp.adapters.ForecastWeatherAdapter;
import com.myapp.weatherapp.models.WeatherItem;
import com.myapp.weatherapp.viewmodels.LoadState;
import com.myapp.weatherapp.viewmodels.WeatherViewModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ForecastWeatherFragment extends Fragment {

    private List<WeatherItem> weatherItems;
    private ForecastWeatherAdapter forecastWeatherAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
        setupViews(view);
        setupAdapter();
        setupWeatherViewModel();
        return view;
    }

    private void setupViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
    }

    private void setupAdapter() {
        weatherItems = new ArrayList<>();
        forecastWeatherAdapter = new ForecastWeatherAdapter(weatherItems, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(forecastWeatherAdapter);
    }

    private void setupWeatherViewModel() {
        WeatherViewModel weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        weatherViewModel.getWeatherItemsLiveData().observe(getViewLifecycleOwner(), this::handleWeatherForecastResponse);
        weatherViewModel.getState().observe(getViewLifecycleOwner(), this::handleLoadState);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleWeatherForecastResponse(List<WeatherItem> newWeatherItems) {
        if (newWeatherItems.size() > 1) {
            weatherItems.clear();
            weatherItems.addAll(newWeatherItems.subList(1, newWeatherItems.size()));
            forecastWeatherAdapter.notifyDataSetChanged();
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
        recyclerView.setVisibility(View.VISIBLE);
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

