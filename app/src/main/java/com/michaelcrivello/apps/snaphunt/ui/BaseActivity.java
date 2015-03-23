package com.michaelcrivello.apps.snaphunt.ui;

import android.os.Bundle;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.event.GcmRegistered;
import com.michaelcrivello.apps.snaphunt.event.GcmUnregistered;
import com.michaelcrivello.apps.snaphunt.util.GcmUtil;
import com.michaelcrivello.apps.snaphunt.util.UserManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboActivity;
import roboguice.util.Ln;

/**
 * Created by michael on 3/19/15.
 */
public class BaseActivity extends RoboActivity{
    @Inject SnaphuntApi snaphuntApi;
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

}
