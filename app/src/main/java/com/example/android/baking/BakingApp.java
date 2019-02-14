package com.example.android.baking;

import android.app.Application;

import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class BakingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
