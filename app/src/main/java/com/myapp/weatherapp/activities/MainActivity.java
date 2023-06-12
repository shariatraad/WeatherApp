package com.myapp.weatherapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.myapp.weatherapp.R;
import com.myapp.weatherapp.adapters.WeatherPagerAdapter;
import com.myapp.weatherapp.utils.Constants;
import com.myapp.weatherapp.viewmodels.LoadState;
import com.myapp.weatherapp.viewmodels.WeatherViewModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


// Main activity of the application
public class MainActivity extends AppCompatActivity {

    private boolean isAutocompleteVisible = true;
    private boolean isSyncing = false;
    private String chosenLocation;

    private ActionBar actionBar;
    private MenuItem syncMenuItem;
    private WeatherViewModel weatherViewModel;
    private AutocompleteSupportFragment autocompleteFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupWeatherViewModel();
        AndroidThreeTen.init(this);
        setupLocation();
        setupPlacePicker();
        setupViewPager();
        setupBottomNavigation();

        // Display API Attribution Dialog only when needed
        if (getShowDialog()) {
            showApiAttributionDialog();
        }
    }

    @Override
    // Handle the result of the location permission request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();  // If permission granted, get the last known location
            } else {
                // If permission not granted, default to a specific location
                chosenLocation = Constants.DEFAULT_CITY;
                setTitleAsCityName(chosenLocation);
                weatherViewModel.setChosenLocation(chosenLocation);
            }
        }
    }

    @Override
    // Inflate the menu items onto the action bar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        syncMenuItem = menu.findItem(R.id.menu_sync);
        View actionView = syncMenuItem.getActionView();
        actionView.setOnClickListener(v -> onOptionsItemSelected(syncMenuItem));
        return true;
    }

    // Handle the user interaction with menu items in the action bar
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // Show or hide the autocomplete feature when the search icon is clicked
        if (itemId == R.id.menu_search) {
            if (isAutocompleteVisible) {
                hideAutocompleteFragment();
            } else {
                showAutocompleteFragment();
            }
            return true;
        } else if (itemId == R.id.menu_sync) {
            // Refresh weather data when sync icon is clicked and not already syncing
            if (!isSyncing) {
                weatherViewModel.refreshData(this);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Initialize views and fetch references to UI elements
    private void setupViews() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }
        // Get the autocomplete fragment reference
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewPager);
    }

    // Setup the ViewPager and its page change listener
    private void setupViewPager() {
        WeatherPagerAdapter weatherPagerAdapter = new WeatherPagerAdapter(this);
        viewPager.setAdapter(weatherPagerAdapter);

        // Add a page change callback to handle UI updates when page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Update the bottom navigation item based on the page selected
                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.current_weather);
                } else {
                    bottomNavigationView.setSelectedItemId(R.id.weather_forecast);
                }
            }
        });
    }

    // Set up the behavior of the bottom navigation bar
    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Change the current item of the ViewPager based on the navigation item selected
            if (item.getItemId() == R.id.current_weather) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.weather_forecast) {
                viewPager.setCurrentItem(1);
                return true;
            }
            return false;
        });
    }

    // Initialize WeatherViewModel and set up its state observer
    private void setupWeatherViewModel() {
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        // Observe changes in the load state of the weather data and start or finish the sync animation
        weatherViewModel.getState().observe(this, loadState -> {
            if (loadState instanceof LoadState.Loading) {
                startSyncAnimation();
            } else {
                finishSyncAnimation();
            }
        });
    }

    // Set up location services and request location permissions if necessary
    private void setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
        } else {
            // If location permission is already granted, get the last known location
            getLastLocation();
        }
    }

    // Set up the autocomplete feature for location search
    private void setupPlacePicker() {
        // Initialize Places API
        Places.initialize(getApplicationContext(), Constants.API_KEY_PLACES);
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        hideAutocompleteFragment();

        // Handle place selection events
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // When a place is selected, update the chosen location and weather data
                chosenLocation = place.getName();
                setTitleAsCityName(chosenLocation);
                weatherViewModel.setChosenLocation(chosenLocation);
                hideAutocompleteFragment();
            }

            // Handle errors during place selection
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

    // Retrieve the flag for showing the API attribution dialog
    private boolean getShowDialog() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return preferences.getBoolean(Constants.DIALOG_KEY_SHARED_PREFERENCES, true);
    }

    // Set the flag for showing the API attribution dialog
    private void setShowDialog(boolean value) {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.DIALOG_KEY_SHARED_PREFERENCES, value);
        editor.apply();
    }

    // Show the API attribution dialog
    private void showApiAttributionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        builder.setView(view);

        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            setShowDialog(!checkBox.isChecked());
        });

        builder.create().show();
    }

    // Get the last known location
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    chosenLocation = getCityName(this, location.getLatitude(), location.getLongitude());
                } else {
                    chosenLocation = Constants.DEFAULT_CITY;
                }
                setTitleAsCityName(chosenLocation);
                weatherViewModel.setChosenLocation(chosenLocation);
            });
        } else {
            chosenLocation = Constants.DEFAULT_CITY;
            setTitleAsCityName(chosenLocation);
            weatherViewModel.setChosenLocation(chosenLocation);
        }
    }

    // Translate coordinates into a city name
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

    // Animate the refresh/sync icon
    public void animateIcon(ImageView imageView) {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setDuration(750);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        if (imageView != null) {
            imageView.clearAnimation();
            imageView.startAnimation(rotateAnimation);
        }
    }

    // Start the sync animation
    private void startSyncAnimation() {
        isSyncing = true;
        if (syncMenuItem != null) {
            View actionView = syncMenuItem.getActionView();
            ImageView imageView = actionView.findViewById(R.id.action_sync_icon);
            actionView.setEnabled(false);
            animateIcon(imageView);
        }
    }

    // Finish the sync animation
    private void finishSyncAnimation() {
        isSyncing = false;
        if (syncMenuItem != null) {
            View actionView = syncMenuItem.getActionView();
            ImageView imageView = actionView.findViewById(R.id.action_sync_icon);
            actionView.setEnabled(true);
            imageView.clearAnimation();
        }
    }

    // Set the title of the action bar as the name of the chosen city
    private void setTitleAsCityName(String chosenLocation) {
        if (actionBar != null && chosenLocation != null) {
            actionBar.setTitle(chosenLocation);
        }
    }

    // Show the autocomplete fragment
    private void showAutocompleteFragment() {
        if (!isAutocompleteVisible) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(autocompleteFragment)
                    .commit();
            isAutocompleteVisible = true;
        }
    }

    // Hide the autocomplete fragment
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
