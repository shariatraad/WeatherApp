package com.myapp.weatherapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.myapp.weatherapp.R;
import com.myapp.weatherapp.adapters.WeatherPagerAdapter;
import com.myapp.weatherapp.models.WeatherItem;
import com.myapp.weatherapp.utils.Constants;
import com.myapp.weatherapp.viewmodels.WeatherViewModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private boolean isAutocompleteVisible = true;
    private boolean isSyncing = false;
    private boolean isFirstSync = true;
    private String chosenLocation;

    private ActionBar actionBar;
    private MenuItem syncMenuItem;
    private WeatherViewModel weatherViewModel;
    private AutocompleteSupportFragment autocompleteFragment;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupWeatherViewModel();
        setupLocation();
        setupPlacePicker();
        setupViewPager();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                chosenLocation = Constants.DEFAULT_CITY;
                getWeatherForecast(chosenLocation);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        syncMenuItem = menu.findItem(R.id.menu_sync);
        View actionView = syncMenuItem.getActionView();
        actionView.setOnClickListener(v -> onOptionsItemSelected(syncMenuItem));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_search) {
            if (isAutocompleteVisible) {
                hideAutocompleteFragment();
            } else {
                showAutocompleteFragment();
            }
            return true;
        } else if (itemId == R.id.menu_sync) {
            if (!isSyncing) {
                getWeatherForecast(chosenLocation);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViews() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    }


    private void setupViewPager() {
        WeatherPagerAdapter weatherPagerAdapter = new WeatherPagerAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(weatherPagerAdapter);
    }

    private void setupWeatherViewModel() {
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        weatherViewModel.getWeatherItemsLiveData().observe(this, this::handleWeatherForecastResponse);
    }

    private void setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getLastLocation();
        }
    }

    private void setupPlacePicker() {
        Places.initialize(getApplicationContext(), Constants.API_KEY_PLACES);
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        hideAutocompleteFragment();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                chosenLocation = place.getName();
                getWeatherForecast(chosenLocation);
                hideAutocompleteFragment();
            }

            @Override
            public void onError(@NonNull Status status) {
                if (status.isCanceled()) {
                    Toast.makeText(MainActivity.this, getString(R.string.autocomplete_canceled), Toast.LENGTH_SHORT).show();
                } else if (status.isInterrupted()) {
                    Toast.makeText(MainActivity.this, getString(R.string.autocomplete_interrupted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    chosenLocation = getCityName(this, location.getLatitude(), location.getLongitude());
                }
                getWeatherForecast(chosenLocation);

            });
        }
    }

    private String getCityName(Context context, double latitude, double longitude) {
        String cityName = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.can_not_find_city_name), Toast.LENGTH_SHORT).show();
        }
        return cityName;
    }

    private void getWeatherForecast(String chosenLocation) {
        if (chosenLocation != null) {
            startSync();
            setTitleAsCityName(chosenLocation);
            weatherViewModel.setChosenLocation(chosenLocation);
            weatherViewModel.getWeatherForecast(this, chosenLocation);
        }
    }

    private void handleWeatherForecastResponse(List<WeatherItem> newWeatherItems) {
        finishSync();
        if (isFirstSync)
            isFirstSync = false;
    }

    public void animateIcon(ImageView imageView) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );
        rotateAnimation.setDuration(750);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        if (imageView != null) {
            imageView.clearAnimation();
            imageView.startAnimation(rotateAnimation);
        }
    }

    private void startSync() {
        isSyncing = true;
        if (syncMenuItem != null) {
            View actionView = syncMenuItem.getActionView();
            ImageView imageView = actionView.findViewById(R.id.action_sync_icon);
            actionView.setEnabled(false);
            animateIcon(imageView);
        }
    }

    private void finishSync() {
        isSyncing = false;
        if (syncMenuItem != null) {
            View actionView = syncMenuItem.getActionView();
            ImageView imageView = actionView.findViewById(R.id.action_sync_icon);
            actionView.setEnabled(true);
            imageView.clearAnimation();
            if (!isFirstSync) {
                Toast.makeText(this, getString(R.string.sync_finished), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTitleAsCityName(String chosenLocation) {
        if (actionBar != null && chosenLocation != null) {
            actionBar.setTitle(chosenLocation);
        }
    }

    private void showAutocompleteFragment() {
        if (!isAutocompleteVisible) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(autocompleteFragment)
                    .commit();
            isAutocompleteVisible = true;
        }
    }

    private void hideAutocompleteFragment() {
        if (isAutocompleteVisible) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(autocompleteFragment)
                    .commit();
            isAutocompleteVisible = false;
        }
    }

}
