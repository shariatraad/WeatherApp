package com.myapp.weatherapp.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        try {
            Date inputDate = inputFormat.parse(date);
            if (inputDate != null) {
                return dateOutputFormat.format(inputDate);
            }
        } catch (ParseException e) {
            Log.e("DateUtils", "Error parsing date: " + date, e);
        }
        return date;
    }

    public static String formatDayOfWeek(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayOutputFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        try {
            Date inputDate = inputFormat.parse(date);
            if (inputDate != null) {
                return dayOutputFormat.format(inputDate);
            }
        } catch (ParseException e) {
            Log.e("DateUtils", "Error parsing date: " + date, e);
        }
        return "";
    }
}
