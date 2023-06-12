package com.myapp.weatherapp.utils;

import com.myapp.weatherapp.BuildConfig;

// Class containing constants used across the application
public class Constants {
    // API key for the weather API, obtained from the build configuration
    public static final String API_KEY_WEATHER = BuildConfig.API_KEY_WEATHER;

    // API key for the places API, obtained from the build configuration
    public static final String API_KEY_PLACES = BuildConfig.API_KEY_PLACES;

    // Base URL for the weather forecast API
    public static final String API_URL_BASE = "https://api.weatherbit.io/v2.0/forecast/daily?city=";

    // Base URL for the current weather API
    public static final String API_URL_BASE_CURRENT = "https://api.weatherbit.io/v2.0/current?city=";

    // Number of days to fetch forecast data for
    public static final String NUMBER_OF_DAYS = "7"; // Maximum 16 days

    // Default city to show weather data for if location permission is not granted
    public static final String DEFAULT_CITY = "Toronto";

    // Name of the shared preferences file
    public static final String SHARED_PREFERENCES_NAME = "preferences";

    // Key to use for the dialog setting in shared preferences
    public static final String DIALOG_KEY_SHARED_PREFERENCES = "showDialog";

    // Request code for location permissions
    public static final int REQUEST_LOCATION_PERMISSION = 1;
}
