package com.gmail.pasquarelli.brandon.destinyapi;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        boolean logCrashes = false;

        if (logCrashes)
            Fabric.with(this, new Crashlytics());
    }
}
