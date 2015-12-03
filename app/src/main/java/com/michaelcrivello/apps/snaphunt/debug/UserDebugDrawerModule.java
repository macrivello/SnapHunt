package com.michaelcrivello.apps.snaphunt.debug;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.databinding.DebugDrawerItemUserBinding;
import com.michaelcrivello.apps.snaphunt.event.UserUpdate;
import com.michaelcrivello.apps.snaphunt.util.UserManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.palaima.debugdrawer.module.DrawerModule;
import roboguice.RoboGuice;

/**
 * Created by tao on 11/12/15.
 */
public class UserDebugDrawerModule implements DrawerModule {
    @Inject UserManager userManager;
    @Inject Bus bus;
    DebugDrawerItemUserBinding debugDrawerUserBinding;

    public UserDebugDrawerModule(Context context) {
        RoboGuice.getInjector(context).injectMembers(this);

        bus.register(this);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        View v =  inflater.inflate(R.layout.debug_drawer_item_user, parent, false);
        debugDrawerUserBinding = DataBindingUtil.inflate(inflater, R.layout.debug_drawer_item_user, parent, false);
        updateUserDataBinding();

        return debugDrawerUserBinding.getRoot();
    }

    @Override
    public void onOpened() {

    }

    @Override
    public void onClosed() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    public void updateUserDataBinding() {
        if (userManager != null && debugDrawerUserBinding != null) {
            debugDrawerUserBinding.setUser(userManager.getUser());
        }
    }

    @Subscribe
    public void onUserUpdate (UserUpdate userUpdate) {
        updateUserDataBinding();
    }
}
