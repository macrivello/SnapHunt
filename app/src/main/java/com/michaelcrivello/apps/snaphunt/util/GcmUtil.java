package com.michaelcrivello.apps.snaphunt.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.service.GcmIntentService;

import roboguice.util.Ln;

/**
 * Created by michael on 3/18/15.
 */
public class GcmUtil {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static String getRegistrationId() {
        // Check if the RegId is stored in sharedPrefs
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Ln.d("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Ln.d("App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion() {
        try {
            PackageInfo packageInfo = SnaphuntApp.getInstance().getPackageManager()
                    .getPackageInfo(SnaphuntApp.getInstance().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    // GCM Related Methods
    public static boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(SnaphuntApp.getInstance());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //TODO: Get this dialog up in an activity
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Ln.e("Need to call GooglePlayServicesUtil.getErrorDialog() in an activity");
            } else {
                Ln.e("Device is not supported");
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    public static void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion();
        Ln.d("Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROPERTY_REG_ID, regId);
        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private static SharedPreferences getGcmPreferences() {
        return SnaphuntApp.getInstance().getSharedPreferences(Constants.GCM_PREFS,
                Context.MODE_PRIVATE);
    }

    public static void register() {
        Intent intent = new Intent(SnaphuntApp.getInstance(), GcmIntentService.class);
        intent.putExtra(Constants.GCM_INTENT_TASK, Constants.GCM_REGISTER);
        SnaphuntApp.getInstance().startService(intent);
    }

    public static void unregister() {
        Intent intent = new Intent(SnaphuntApp.getInstance(), GcmIntentService.class);
        intent.putExtra(Constants.GCM_INTENT_TASK, Constants.GCM_UNREGISTER);
        SnaphuntApp.getInstance().startService(intent);
    }
}
