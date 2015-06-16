package com.michaelcrivello.apps.snaphunt.bus;

import android.os.Handler;
import android.os.Looper;
import roboguice.util.Ln;

import com.squareup.otto.Bus;

/**
 * Created by miccrive on 3/14/15.
 */
public class MainThreadBus extends Bus {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MainThreadBus.super.post(event);
                }
            });
        }
    }

    // Simple way to prevent registering the object twice - just catch the exeption thrown
    @Override
    public void register(Object object) {
        try {
            super.register(object);
        } catch (Exception e) {
            Ln.d("Unable to register object: %s. Error: %s", object.getClass().toString(), e.getMessage());
        }
    }

    // Simple way to prevent unregistering the object twice - just catch the exeption thrown
    @Override
    public void unregister(Object object) {
        try {
            super.unregister(object);
        } catch (Exception e) {
            Ln.d("Unable to unregister object: %s. Error: %s", object.getClass().toString(), e.getMessage());
        }    }
}