package com.myapp.weatherapp.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// Singleton class for initializing and accessing a shared Volley RequestQueue.
public class RequestQueueSingleton {
    private static RequestQueue instance;

    // Private constructor to prevent instantiation
    private RequestQueueSingleton() {
    }

    // Get the singleton instance, if it does not exist it is created.
    public static synchronized RequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = Volley.newRequestQueue(context.getApplicationContext());
        }
        return instance;
    }
}
