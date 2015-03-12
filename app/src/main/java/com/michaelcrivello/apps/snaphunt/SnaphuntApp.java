package com.michaelcrivello.apps.snaphunt;

import android.app.Application;
import android.os.StrictMode;

import com.google.inject.Singleton;
import com.squareup.otto.Bus;


/**
 * Created by michael on 3/11/15.
 */
public class SnaphuntApp extends Application {


    @Override public void onCreate() {
        super.onCreate();
    }

    @Singleton
    Bus getBus() {
        return new Bus();
    }
}
