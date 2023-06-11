package com.myapp.weatherapp.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.VolleyError;
import com.myapp.weatherapp.models.WeatherItem;
import com.myapp.weatherapp.network.WeatherService;

import java.util.List;

public class WeatherViewModel extends ViewModel {
    private final MutableLiveData<List<WeatherItem>> weatherItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<LoadState> state = new MutableLiveData<>();
    private final WeatherService weatherService = new WeatherService();

    public LiveData<List<WeatherItem>> getWeatherItemsLiveData() {
        return weatherItemsLiveData;
    }

    public LiveData<LoadState> getState() {
        return state;
    }

    public void getWeatherForecast(Context context, String location) {
        state.setValue(new LoadState.Loading());
        weatherService.getWeatherForecast(context, location,
                this::handleWeatherForecastResponse,
                this::handleWeatherForecastError);
    }

    private void handleWeatherForecastResponse(List<WeatherItem> newWeatherItems) {
        state.postValue(new LoadState.Success());
        weatherItemsLiveData.postValue(newWeatherItems);
    }

    private void handleWeatherForecastError(VolleyError error) {
        state.postValue(new LoadState.Error(error));
    }
}


