package com.app.hometohomefreebies.app;

import android.app.Application;

import com.app.hometohomefreebies.R;
import com.google.android.libraries.places.api.Places;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(this, getString(R.string.google_apis_key));
    }
}
