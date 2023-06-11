package com.myapp.weatherapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.myapp.weatherapp.fragments.CurrentWeatherFragment;
import com.myapp.weatherapp.fragments.ForecastWeatherFragment;

public class WeatherPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    public WeatherPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new CurrentWeatherFragment();
        } else {
            return new ForecastWeatherFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}