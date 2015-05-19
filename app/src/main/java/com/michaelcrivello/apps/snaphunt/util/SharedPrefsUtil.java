package com.michaelcrivello.apps.snaphunt.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;

/**
 * Created by michael on 5/12/15.
 */
public class SharedPrefsUtil {
    public static SharedPreferences sharedPreferences
            = SnaphuntApp.getInstance().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);

}
