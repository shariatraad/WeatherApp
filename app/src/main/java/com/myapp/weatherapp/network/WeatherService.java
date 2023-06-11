package com.myapp.weatherapp.network;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.myapp.weatherapp.models.WeatherItem;
import com.myapp.weatherapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherService {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public WeatherService() {
    }

    public void getWeatherForecast(Context context, String chosenLocation, Response.Listener<List<WeatherItem>> listener, Response.ErrorListener errorListener) {
        String url = Constants.API_URL_BASE + chosenLocation + "&days=" + Constants.NUMBER_OF_DAYS + "&key=" + Constants.API_KEY_WEATHER;

        RequestQueue queue = RequestQueueSingleton.getInstance(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> executorService.execute(() -> {
                    try {
                        List<WeatherItem> weatherItems = parseWeatherForecastResponse(response);
                        ((Activity) context).runOnUiThread(() -> listener.onResponse(weatherItems));
                    } catch (JSONException e) {
                        ((Activity) context).runOnUiThread(() -> errorListener.onErrorResponse(new VolleyError(e)));
                    }
                }), errorListener);

        queue.add(stringRequest);
    }

    public List<WeatherItem> parseWeatherForecastResponse(String jsonResponse) throws JSONException {
        JSONObject response = new JSONObject(jsonResponse);
        JSONArray forecastData = response.getJSONArray("data");
        String timeZoneString = response.getString("timezone");

        List<WeatherItem> newWeatherItems = new ArrayList<>();
        for (int i = 0; i < forecastData.length(); i++) {
            JSONObject forecastObject = forecastData.getJSONObject(i);

            String dateString = forecastObject.getString("valid_date");
            float tempMin = (float) forecastObject.getDouble("min_temp");
            float tempMax = (float) forecastObject.getDouble("max_temp");
            float precipitation = (float) forecastObject.getDouble("precip");
            String weatherCode = forecastObject.getJSONObject("weather").getString("code");

            WeatherItem weatherItem = new WeatherItem(dateString, timeZoneString, tempMin, tempMax, precipitation, weatherCode);
            newWeatherItems.add(weatherItem);
        }
        return newWeatherItems;
    }

    public void getCurrentWeather(Context context, String chosenLocation, Response.Listener<WeatherItem> listener, Response.ErrorListener errorListener) {
        String url = Constants.API_URL_BASE_CURRENT + chosenLocation + "&key=" + Constants.API_KEY_WEATHER;

        RequestQueue queue = RequestQueueSingleton.getInstance(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> executorService.execute(() -> {
                    try {
                        WeatherItem weatherItem = parseCurrentWeatherResponse(response);
                        ((Activity) context).runOnUiThread(() -> listener.onResponse(weatherItem));
                    } catch (JSONException e) {
                        ((Activity) context).runOnUiThread(() -> errorListener.onErrorResponse(new VolleyError(e)));
                    }
                }), errorListener);

        queue.add(stringRequest);
    }

    public WeatherItem parseCurrentWeatherResponse(String jsonResponse) throws JSONException {
        JSONObject response = new JSONObject(jsonResponse);
        JSONArray dataArray = response.getJSONArray("data");
        JSONObject weatherData = dataArray.getJSONObject(0);

        String dateString = weatherData.getString("datetime");
        String sunriseString = weatherData.getString("sunrise");
        String sunsetString = weatherData.getString("sunset");
        String timeZoneString = weatherData.getString("timezone");
        float tempCurrent = (float) weatherData.getDouble("temp");
        String weatherCode = weatherData.getJSONObject("weather").getString("code");
        return new WeatherItem(dateString, sunriseString, sunsetString, timeZoneString, tempCurrent, weatherCode);
    }
}
