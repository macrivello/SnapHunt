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
}