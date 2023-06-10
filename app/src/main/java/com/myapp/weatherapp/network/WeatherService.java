package com.myapp.weatherapp.network;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

    private static final String API_KEY = Constants.API_KEY_WEATHER;
    private static final String API_URL_BASE = Constants.API_URL_BASE;
    private static final String NUMBER_OF_DAYS = Constants.NUMBER_OF_DAYS;

    public WeatherService() {
    }

    public void getWeatherForecast(Context context, String chosenLocation, Response.Listener<List<WeatherItem>> listener, Response.ErrorListener errorListener) {
        String url = API_URL_BASE + chosenLocation + "&days=" + NUMBER_OF_DAYS + "&key=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {
                        try {
                            List<WeatherItem> weatherItems = parseWeatherForecastResponse(response);
                            ((Activity) context).runOnUiThread(() -> listener.onResponse(weatherItems));
                        } catch (JSONException e) {
                            ((Activity) context).runOnUiThread(() -> errorListener.onErrorResponse(new VolleyError(e)));
                        }
                    });
                }, errorListener);

        queue.add(stringRequest);
    }


    public List<WeatherItem> parseWeatherForecastResponse(String jsonResponse) throws JSONException {
        JSONObject response = new JSONObject(jsonResponse);
        JSONArray forecastData = response.getJSONArray("data");
        List<WeatherItem> newWeatherItems = new ArrayList<>();
        for (int i = 0; i < forecastData.length(); i++) {
            JSONObject forecastObject = forecastData.getJSONObject(i);

            // Extract forecast data
            String dateString = forecastObject.getString("valid_date");
            float tempMin = (float) forecastObject.getDouble("min_temp");
            float tempMax = (float) forecastObject.getDouble("max_temp");
            float precipitation = (float) forecastObject.getDouble("precip");
            String weatherCode = forecastObject.getJSONObject("weather").getString("code");

            WeatherItem weatherItem = new WeatherItem(dateString, tempMin, tempMax, precipitation, weatherCode);
            newWeatherItems.add(weatherItem);
        }
        return newWeatherItems;
    }


}
