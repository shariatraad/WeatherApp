package com.myapp.weatherapp.utils;

import com.myapp.weatherapp.BuildConfig;

public class Constants {
    public static final String API_KEY_WEATHER = BuildConfig.API_KEY_WEATHER;
    public static final String API_KEY_PLACES = BuildConfig.API_KEY_PLACES;
    public static final String API_URL_BASE = "https://api.weatherbit.io/v2.0/forecast/daily?city=";
    public static final String NUMBER_OF_DAYS = "7"; //Maximum 16 days
    public static final String DEFAULT_CITY = "Toronto";
    public static final String SHARED_PREFERENCES_NAME = "preferences";
    public static final String DIALOG_KEY_SHARED_PREFERENCES = "showDialog";
    public static final int REQUEST_LOCATION_PERMISSION = 1;
}