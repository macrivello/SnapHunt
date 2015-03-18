package com.michaelcrivello.apps.snaphunt.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by miccrive on 3/17/15.
 */
public class SnaphuntApiProvider implements Provider<SnaphuntApi> {
    @Inject ApiHeaders requestInterceptor;

    @Override
    public SnaphuntApi get() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint(SnaphuntApi.API_ENDPOINT)
                .setConverter(SnaphuntApi.DATA_CONVERTER)
                .setRequestInterceptor(requestInterceptor)
                .build();

        return restAdapter.create(SnaphuntApi.class);
    }
}
