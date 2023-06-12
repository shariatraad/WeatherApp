package com.myapp.weatherapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.myapp.weatherapp.fragments.CurrentWeatherFragment;
import com.myapp.weatherapp.fragments.ForecastWeatherFragment;

// Adapter for ViewPager2 returning a fragment corresponding to one of the tabs
public class WeatherPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    // Constructor for the WeatherPagerAdapter.
    public WeatherPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    // Create a new fragment for the specified position
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new CurrentWeatherFragment();  // Return the CurrentWeatherFragment for the first tab.
        } else {
            return new ForecastWeatherFragment();  // Return the ForecastWeatherFragment for the second tab.
        }
    }

    @Override
    // Return the total number of pages
    public int getItemCount() {
        return NUM_PAGES;
    }
}
