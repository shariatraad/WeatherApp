package com.myapp.weatherapp.viewmodels;

// Abstract class representing the state of a loading operation
public abstract class LoadState {

    // Private constructor to prevent instantiation
    private LoadState() {
    }

    // Successful completion of a loading operation
    public static class Success extends LoadState {
    }

    // Loading operation that is currently in progress
    public static class Loading extends LoadState {
    }

    // Nested class representing a loading operation that has encountered an error
    public static class Error extends LoadState {

        // Error occurred during the loading operation
        public final Throwable error;

        // Constructor for creating an Error state with a given Throwable error
        public Error(Throwable error) {
            this.error = error;
        }
    }
}
