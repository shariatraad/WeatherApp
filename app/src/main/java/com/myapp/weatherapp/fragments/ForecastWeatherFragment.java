package com.myapp.weatherapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

// Fragment displaying the forecasted weather information
public class ForecastWeatherFragment extends Fragment {

    private List<WeatherItem> weatherItems;
    private ForecastWeatherAdapter forecastWeatherAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    // Inflate the layout for this fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
        setupViews(view);
        setupWeatherViewModel();
        setupAdapter();
        return view;
    }

    // Find and assign all the UI elements
    private void setupViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
    }

    // Setup the RecyclerView and its adapter
    private void setupAdapter() {
        weatherItems = new ArrayList<>();
        forecastWeatherAdapter = new ForecastWeatherAdapter(weatherItems, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(forecastWeatherAdapter);
    }

    // Setup the ViewModel and LiveData observer for the fragment
    private void setupWeatherViewModel() {
        WeatherViewModel weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        weatherViewModel.getWeatherForecastLiveData().observe(getViewLifecycleOwner(), this::handleWeatherForecastResponse);
        weatherViewModel.getState().observe(getViewLifecycleOwner(), this::handleLoadState);
        weatherViewModel.getChosenLocation().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newLocation) {
                weatherViewModel.getWeatherForecast(requireActivity());
            }
        });
    }

    // Handle the response from the ViewModel's LiveData for the forecasted weather
    @SuppressLint("NotifyDataSetChanged")
    private void handleWeatherForecastResponse(List<WeatherItem> newWeatherItems) {
        if (newWeatherItems.size() > 1) {
            weatherItems.clear();
            weatherItems.addAll(newWeatherItems);
            forecastWeatherAdapter.notifyDataSetChanged();
        }
    }

    // Handle the current loading state and update the UI accordingly
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

    // Show the loading state UI
    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
    }

    // Hide the loading state UI
    private void hideLoadingState() {
        progressBar.setVisibility(View.GONE);
    }

    // Show the success state UI
    private void handleSuccessState() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    // Handle any error that might have occurred during loading and display the appropriate message
    private void handleVolleyError(Throwable error) {
        String errorMessage = getErrorMessage(error);
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Get the error message based on the type of error that occurred
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

