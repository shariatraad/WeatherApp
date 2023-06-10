package com.myapp.weatherapp.utils;

import com.myapp.weatherapp.R;


public class WeatherIconMapper {

    public static int getWeatherIconResource(String conditionCode) {
        if (conditionCode.equals("800")) {
            return R.drawable.icon_clear_sky;
        } else if (conditionCode.charAt(0) == '8') {
            return R.drawable.icon_cloudy;
        } else {
            switch (conditionCode.charAt(0)) {
                case '2':
                    return R.drawable.icon_thunderstorm;
                case '3':
                    return R.drawable.icon_drizzle;
                case '5':
                case '9': // Rain + Unknown Precipitation
                    return R.drawable.icon_rain;
                case '6':
                    return R.drawable.icon_snow;
                case '7':
                    return R.drawable.icon_fog;
                default:
                    return R.drawable.icon_clear_sky;
            }
        }
    }
}
