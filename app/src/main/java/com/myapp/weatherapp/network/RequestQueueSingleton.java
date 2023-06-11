package com.myapp.weatherapp.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {
    private static RequestQueue instance;

    private RequestQueueSingleton() {
    }

    public static synchronized RequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = Volley.newRequestQueue(context.getApplicationContext());
        }
        return instance;
    }
}
