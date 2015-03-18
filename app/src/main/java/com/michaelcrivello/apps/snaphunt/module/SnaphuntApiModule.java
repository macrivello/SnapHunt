package com.michaelcrivello.apps.snaphunt.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;

import retrofit.RestAdapter;

/**
 * Created by miccrive on 3/17/15.
 */
public class SnaphuntApiModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(SnaphuntApi.class)
                .toProvider(SnaphuntApiProvider.class)
                .asEagerSingleton();
    }
}
