package com.michaelcrivello.apps.snaphunt.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.event.ApiRequest;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.service.RoboService;
import roboguice.util.Ln;

/**
 * Created by miccrive on 3/17/15.
 */
public class SnaphuntApiSerivce extends RoboService {
    @Inject Bus bus;
    @Inject SnaphuntApi snaphuntApi;
    Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Ln.d("Creating SnaphuntApiService");
        super.onCreate();
        context = getApplicationContext();
        bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Subscribe
    public void getUsers(ApiRequest apiRequest) {

    }
}
