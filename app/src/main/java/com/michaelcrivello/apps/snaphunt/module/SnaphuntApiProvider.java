package com.michaelcrivello.apps.snaphunt.module;

import com.google.inject.Provider;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;

import retrofit.RestAdapter;

/**
 * Created by miccrive on 3/17/15.
 */
public class SnaphuntApiProvider implements Provider<SnaphuntApi> {

    @Override
    public SnaphuntApi get() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SnaphuntApi.API_ENDPOINT)
                .setConverter(SnaphuntApi.DATA_CONVERTER)
                .build();

        return restAdapter.create(SnaphuntApi.class);
    }
}
