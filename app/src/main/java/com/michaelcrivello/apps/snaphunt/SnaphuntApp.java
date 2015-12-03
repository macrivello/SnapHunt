package com.michaelcrivello.apps.snaphunt;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.google.inject.Provides;
import com.michaelcrivello.apps.snaphunt.service.GCMInstanceIDListenerService;
import com.michaelcrivello.apps.snaphunt.service.GCMListenerService;
import com.michaelcrivello.apps.snaphunt.service.S3TransferService;

import io.palaima.debugdrawer.DebugDrawer;
import roboguice.util.Ln;


/**
 * Created by michael on 3/11/15.
 */
public class SnaphuntApp extends MultiDexApplication {
    static SnaphuntApp instance;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;

        startServices();
    }

    public void startServices() {
        Ln.d("Making call to start services");

        startService(new Intent(this, S3TransferService.class));
    }

    public static SnaphuntApp getInstance() { return instance; }
}
