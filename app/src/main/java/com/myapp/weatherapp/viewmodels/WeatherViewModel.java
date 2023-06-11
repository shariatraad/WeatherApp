package com.myapp.weatherapp.viewmodels;

import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.myapp.weatherapp.models.WeatherItem;
import com.myapp.weatherapp.network.WeatherService;

import java.lang.ref.WeakReference;
import java.util.List;

public class WeatherViewModel extends ViewModel {
    private final MutableLiveData<List<WeatherItem>> weatherForecastLiveData = new MutableLiveData<>();
    private final MutableLiveData<WeatherItem> currentWeatherLiveData = new MutableLiveData<>();
    private final MutableLiveData<LoadState> state = new MutableLiveData<>();
    private final WeatherService weatherService = new WeatherService();
    private final MutableLiveData<String> chosenLocation = new MutableLiveData<>();

    private final int MAX_RETRIES = 3;
    private final long RETRY_DELAY_MS = 2000;
    private int retryCountCurrentWeather = 0;
    private int retryCountWeatherForecast = 0;

    private WeakReference<Context> contextRef;
    private final Handler retryHandler = new Handler();

    public LiveData<List<WeatherItem>> getWeatherForecastLiveData() {
        return weatherForecastLiveData;
    }

    public LiveData<WeatherItem> getCurrentWeatherLiveData() {
        return currentWeatherLiveData;
    }

    public LiveData<LoadState> getState() {
        return state;
    }

    public void getWeatherForecast(Context context) {
        this.contextRef = new WeakReference<>(context);
        state.setValue(new LoadState.Loading());
        String location = chosenLocation.getValue();
        weatherService.getWeatherForecast(context, location,
                this::handleWeatherForecastResponse,
                this::handleWeatherForecastError);
    }

    public void setChosenLocation(String newLocation) {
        chosenLocation.postValue(newLocation);
    }

    public LiveData<String> getChosenLocation() {
        return chosenLocation;
    }

    public void refreshData(Context context) {
        getCurrentWeather(context);
        getWeatherForecast(context);
    }

    public void getCurrentWeather(Context context) {
        this.contextRef = new WeakReference<>(context);
        String location = chosenLocation.getValue();
        if (location != null) {
            state.setValue(new LoadState.Loading());
            weatherService.getCurrentWeather(context, location,
                    this::handleCurrentWeatherResponse,
                    this::handleCurrentWeatherError);
        }
    }

    private void handleWeatherForecastResponse(List<WeatherItem> newWeatherItems) {
        state.postValue(new LoadState.Success());
        weatherForecastLiveData.postValue(newWeatherItems);
        retryCountWeatherForecast = 0;
    }

    private void handleWeatherForecastError(VolleyError error) {
        if ((error instanceof TimeoutError || error instanceof NoConnectionError)
                && retryCountWeatherForecast < MAX_RETRIES) {
            retryCountWeatherForecast++;
            retryHandler.postDelayed(() -> getWeatherForecast(contextRef.get()), RETRY_DELAY_MS);
        } else {
            state.postValue(new LoadState.Error(error));
            retryCountWeatherForecast = 0;
        }
    }

    private void handleCurrentWeatherResponse(WeatherItem currentWeather) {
        state.postValue(new LoadState.Success());
        currentWeatherLiveData.postValue(currentWeather);
        retryCountCurrentWeather = 0;
    }

    private void handleCurrentWeatherError(VolleyError error) {
        if ((error instanceof TimeoutError || error instanceof NoConnectionError)
                && retryCountCurrentWeather < MAX_RETRIES) {
            retryCountCurrentWeather++;
            retryHandler.postDelayed(() -> getCurrentWeather(contextRef.get()), RETRY_DELAY_MS);
        } else {
            state.postValue(new LoadState.Error(error));
            retryCountCurrentWeather = 0;
        }
    }
}

