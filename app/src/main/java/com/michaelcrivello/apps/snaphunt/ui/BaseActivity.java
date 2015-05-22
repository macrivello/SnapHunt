package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.api.Api;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.event.GcmRegistered;
import com.michaelcrivello.apps.snaphunt.event.GcmUnregistered;
import com.michaelcrivello.apps.snaphunt.util.GcmUtil;
import com.michaelcrivello.apps.snaphunt.util.UserManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboActionBarActivity;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboFragmentActivity;
import roboguice.fragment.RoboFragment;
import roboguice.util.Ln;

/**
 * Created by michael on 3/19/15.
 */
public class BaseActivity extends RoboActionBarActivity {
    @Inject SnaphuntApi snaphuntApi;
    @Inject ApiHeaders apiHeaders;
    @Inject Bus bus;
    @Inject UserManager userManager;
    BaseActivityBusListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        bus.register(listener = new BaseActivityBusListener());

        if (GcmUtil.getRegistrationId().isEmpty()) {
            GcmUtil.register();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
        bus.unregister(listener);
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
    }

    protected void logout() {
        userManager.clearUser();
        startActivity(new Intent(this, WelcomeActivity.class));
        overridePendingTransition(0, 0);
    }
}
