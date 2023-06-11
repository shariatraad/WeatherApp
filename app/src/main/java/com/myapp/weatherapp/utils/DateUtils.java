package com.myapp.weatherapp.utils;

import android.util.Log;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.Locale;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    public static String formatDayOfWeekCurrent(String dateTimeStr, String timeZoneId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

            ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(timeZoneId));

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
            return outputFormatter.format(zonedDateTime);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting day of week: " + e.getMessage());
            return null;
        }
    }

    public static String formatDateCurrent(String dateTimeStr, String timeZoneId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

            ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(timeZoneId));

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);
            return outputFormatter.format(zonedDateTime);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date: " + e.getMessage());
            return null;
        }
    }

    public static String formatDayOfWeekForecast(String dateStr, String timeZoneId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(dateStr, formatter);

            ZonedDateTime zonedDateTime = date.atTime(12, 0, 0).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(timeZoneId));

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
            return outputFormatter.format(zonedDateTime);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting day of week: " + e.getMessage());
            return null;
        }
    }

    public static String formatDateForecast(String dateStr, String timeZoneId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(dateStr, formatter);

            ZonedDateTime zonedDateTime = date.atTime(12, 0, 0).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(timeZoneId));

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);
            return outputFormatter.format(zonedDateTime);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date: " + e.getMessage());
            return null;
        }
    }

    public static String convertUtcToLocal(String utcTimeStr, String timeZoneId) {
        try {
            LocalTime utcTime = LocalTime.parse(utcTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            ZoneId zoneId = ZoneId.of(timeZoneId);
            ZonedDateTime zonedDateTime = utcTime.atDate(LocalDate.now()).atZone(ZoneId.of("UTC")).withZoneSameInstant(zoneId);
            return zonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } catch (DateTimeParseException e) {
            Log.e(TAG, "Error converting UTC to local time: " + e.getMessage());
            return null;
        }
    }
}
