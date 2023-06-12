package com.myapp.weatherapp.models;

// Class that represents an item of weather data
public class WeatherItem {
    private final String date;
    private final Float tempMin;
    private final Float tempMax;
    private final Float tempCurrent;
    private final Float precipitation;
    private final String weatherCode;
    private final String sunrise;
    private final String sunset;
    private final String timeZone;

    // Constructor used for creating a WeatherItem object for forecasted weather data
    public WeatherItem(String date, String timeZone, Float tempMin, Float tempMax, float precipitation, String weatherCode) {
        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.precipitation = precipitation;
        this.weatherCode = weatherCode;
        this.timeZone = timeZone;
        this.tempCurrent = null;
        this.sunrise = null;
        this.sunset = null;

    }

    // Constructor used for creating a WeatherItem object for current weather data
    public WeatherItem(String date, String sunrise, String sunset, String timeZone, float tempCurrent, String weatherCode) {
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.timeZone = timeZone;
        this.tempCurrent = tempCurrent;
        this.weatherCode = weatherCode;
        this.precipitation = null;
        this.tempMin = null;
        this.tempMax = null;
    }

    public String getDate() {
        return date;
    }

    public Float getTempMin() {
        return tempMin;
    }

    public Float getTempMax() {
        return tempMax;
    }

    public float getPrecipitation() {
        return precipitation;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public float getTempCurrent() {
        return tempCurrent;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getTimeZone() {
        return timeZone;
    }
}



