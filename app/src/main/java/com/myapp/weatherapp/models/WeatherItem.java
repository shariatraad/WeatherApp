package com.myapp.weatherapp.models;

public class WeatherItem {
    private final String date;
    private final float tempMin;
    private final float tempMax;
    private final float precipitation;
    private final String weatherCode;

    public WeatherItem(String date, float tempMin, float tempMax, float precipitation, String weatherCode) {
        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.precipitation = precipitation;
        this.weatherCode = weatherCode;

    }

    public String getDate() {
        return date;
    }

    public float getTempMin() {
        return tempMin;
    }

    public float getTempMax() {
        return tempMax;
    }

    public float getPrecipitation() {
        return precipitation;
    }

    public String getWeatherCode() {
        return weatherCode;
    }
}


