package com.michaelcrivello.apps.snaphunt.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.SharedPrefsUtil;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by miccrive on 3/17/15.
 */
public class SnaphuntApiProvider implements Provider<SnaphuntApi> {
    @Inject ApiHeaders requestInterceptor;

    @Override
    public SnaphuntApi get() {

        String endpoint = SharedPrefsUtil.sharedPreferences.getString(Constants.API_ENDPOINT_KEY, "");
        if (endpoint.isEmpty()) {
            SharedPrefsUtil.sharedPreferences.edit().putString(Constants.API_ENDPOINT_KEY, SnaphuntApi.API_ENDPOINT).apply();
            endpoint = SnaphuntApi.API_ENDPOINT;
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(endpoint)
                .setConverter(SnaphuntApi.DATA_CONVERTER)
                .setRequestInterceptor(requestInterceptor)
                .build();

        return restAdapter.create(SnaphuntApi.class);
    }
}
