package com.myapp.weatherapp.viewmodels;

public abstract class LoadState {
    private LoadState() {
    }

    public static class Success extends LoadState {
    }

    public static class Loading extends LoadState {
    }

    public static class Error extends LoadState {
        public final Throwable error;

        public Error(Throwable error) {
            this.error = error;
        }
    }
}
