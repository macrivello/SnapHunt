package com.michaelcrivello.apps.snaphunt.ui.fragments;

import com.google.inject.Inject;
import com.squareup.otto.Bus;

import roboguice.fragment.RoboFragment;

/**
 * Created by michael on 5/20/15.
 */
public abstract class BaseFragment extends RoboFragment {
    @Inject
    Bus bus;

    public abstract String getTitle();

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    public abstract void autoRefresh(boolean b);
}
