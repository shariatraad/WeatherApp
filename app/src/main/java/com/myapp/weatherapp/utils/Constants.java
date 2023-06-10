package com.myapp.weatherapp.utils;

import com.myapp.weatherapp.BuildConfig;

public class Constants {
    public static final String API_KEY_WEATHER = BuildConfig.API_KEY_WEATHER;
    public static final String API_KEY_PLACES = BuildConfig.API_KEY_PLACES;
    public static final String API_URL_BASE = "https://api.weatherbit.io/v2.0/forecast/daily?city=";
    public static final String NUMBER_OF_DAYS = "7"; //Maximum 16 days
    public static final String DEFAULT_CITY = "Toronto";
}