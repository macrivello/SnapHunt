package com.michaelcrivello.apps.snaphunt.data.api;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.util.Constants;

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by miccrive on 3/17/15.
 */
public interface SnaphuntApi {
    Converter DATA_CONVERTER = new GsonConverter(new Gson());
    String API_VERSION = "v1";
    String API_ENDPOINT = "http://localhost:3000/api/" + API_VERSION + "/";
    String AUTH_HEADER = Constants.AUTH_HEADER;

    // User
    @GET("/users")
    List<User> listUsers();

}
