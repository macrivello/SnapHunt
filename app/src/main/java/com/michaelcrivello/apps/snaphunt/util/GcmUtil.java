package com.michaelcrivello.apps.snaphunt.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.service.GCMInstanceIDRegistrationIntentService;

import java.io.IOException;

import roboguice.util.Ln;

/**
 * Created by michael on 3/18/15.
 */
public class GCMUtil {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static String getRegistrationId() {
        return SharedPrefsUtil.sharedPreferences.getString(Constants.GCM_TOKEN_KEY, "");
    }

    public static void register() {
        Intent intent = new Intent(SnaphuntApp.getInstance(), GCMInstanceIDRegistrationIntentService.class);
        SnaphuntApp.getInstance().startService(intent);
    }

    public static void unregister() {
        /* YOU CAN NEVER UNREGISTER MUAHAHA */
    }

}
