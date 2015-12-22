package com.michaelcrivello.apps.snaphunt.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.michaelcrivello.apps.snaphunt.BuildConfig;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.debug.ApiEndpointDebugDrawerModule;
import com.michaelcrivello.apps.snaphunt.debug.UserDebugDrawerModule;
import com.michaelcrivello.apps.snaphunt.event.AutoRefresh;
import com.michaelcrivello.apps.snaphunt.event.GcmRegistered;
import com.michaelcrivello.apps.snaphunt.event.GcmUnregistered;
import com.michaelcrivello.apps.snaphunt.event.S3TransferManagerUpdated;
import com.michaelcrivello.apps.snaphunt.service.GCMInstanceIDRegistrationIntentService;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.SharedPrefsUtil;
import com.michaelcrivello.apps.snaphunt.util.UserManager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.module.BuildModule;
import io.palaima.debugdrawer.module.DeviceModule;
import io.palaima.debugdrawer.module.NetworkModule;
import io.palaima.debugdrawer.module.SettingsModule;
import io.palaima.debugdrawer.okhttp.OkHttpModule;
import io.palaima.debugdrawer.picasso.PicassoModule;
import roboguice.activity.RoboActionBarActivity;
import roboguice.util.Ln;

/**
 * Created by michael on 3/19/15.
 */
public abstract class BaseActivity extends RoboActionBarActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Inject SnaphuntApi snaphuntApi;
    @Inject ApiHeaders apiHeaders;
    @Inject Bus bus;
    @Inject UserManager userManager;
    @Inject Picasso picasso;
    @Inject OkHttpClient okHttpClient;

    BaseActivityBusListener baseListener;
    TransferManager transferManager;
    Context context;
    DebugDrawer debugDrawer;

    protected boolean autoRefresh;
    private boolean playServicesInstalled;
    private BroadcastReceiver registrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getBaseContext();
        baseListener = new BaseActivityBusListener();

        playServicesInstalled = checkPlayServices();
        SharedPrefsUtil.sharedPreferences.edit().putBoolean(Constants.PLAY_SERVICES_INSTALLED_KEY, playServicesInstalled).apply();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, GCMInstanceIDRegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(baseListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(baseListener);
    }

    protected void initDebugDrawer() {
        if (BuildConfig.DEBUG) {
            debugDrawer = new DebugDrawer.Builder(this).modules(
                    new ApiEndpointDebugDrawerModule(this),
                    new UserDebugDrawerModule(this),
                    new OkHttpModule(okHttpClient),
                    new PicassoModule(picasso),
                    new DeviceModule(this),
                    new BuildModule(this),
                    new NetworkModule(this),
                    new SettingsModule(this)
            ).build();
        }
    }

    protected class BaseActivityBusListener {
        @Subscribe
        public void onGcmRegistered (GcmRegistered gcmRegistered) {
            Ln.d("onGcmRegistered");
            userManager.updateUserGcmId(gcmRegistered.getRegId());
        }
        @Subscribe
        public void onGcmUnregistered (GcmUnregistered gcmUnregistered) {
            Ln.d("onGcmUnregistered");
        }
        @Subscribe
        public void updateTransferManager (S3TransferManagerUpdated transferManagerUpdated) {
            Ln.d("updateTransferManager");
            transferManager = transferManagerUpdated.getTransferManager();
        }

        @Subscribe
        public void onAutoRefresh(AutoRefresh autoRefresh) {
            autoRefresh(autoRefresh.isAutoRefresh());
        }

        @Provides
        public AutoRefresh onAutoRefreshProvides() {
            return new AutoRefresh(autoRefresh);
        }

    }

    protected void logout() {
        userManager.clearUser();
        startActivity(new Intent(this, WelcomeActivity.class));
        overridePendingTransition(0, 0);
    }


    protected abstract void autoRefresh(boolean b);

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Ln.i("This device is not supported.");
                return false;
            }
            return false;
        }
        return true;
    }
}
