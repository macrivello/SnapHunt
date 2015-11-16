package com.michaelcrivello.apps.snaphunt.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.michaelcrivello.apps.snaphunt.bus.MainThreadBus;
import com.squareup.otto.Bus;

/**
 * Created by miccrive on 3/14/15.
 */
public class BusModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(Bus.class).to(MainThreadBus.class).asEagerSingleton();
    }
}
