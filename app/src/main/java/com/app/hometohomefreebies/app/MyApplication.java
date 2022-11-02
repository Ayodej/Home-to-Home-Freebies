package com.app.hometohomefreebies.app;

import android.app.Application;

import com.app.hometohomefreebies.R;
import com.google.android.libraries.places.api.Places;
import com.onesignal.OneSignal;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(this, getString(R.string.google_apis_key));

        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Config.pusher = new Pusher("8a7300fc680d8bd7ec1b", options);
        Config.pusher.connect();

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("701d6de3-03bf-4684-9f1c-9e463e29473d");
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
    }
}
