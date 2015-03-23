package com.michaelcrivello.apps.snaphunt;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Singleton;
import com.michaelcrivello.apps.snaphunt.event.S3TransferProgress;
import com.michaelcrivello.apps.snaphunt.service.S3TransferService;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.GcmUtil;
import com.squareup.otto.Bus;

import java.io.IOException;

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
