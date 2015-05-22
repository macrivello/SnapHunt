package com.michaelcrivello.apps.snaphunt.data.api;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.GsonUtil;
import com.squareup.okhttp.Call;

import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by miccrive on 3/17/15.
 */
public interface SnaphuntApi {
    Converter DATA_CONVERTER = new GsonConverter(GsonUtil.getMongoDocGson());
    String API_VERSION = "v1";
    // 10.0.2.2 is this machine localhost http://developer.android.com/tools/devices/emulator.html#networkaddresses
    String API_ENDPOINT = "http://10.0.2.2:3000/api/" + API_VERSION + "/";
    String AUTH_HEADER = Constants.AUTH_HEADER;

    // User
    @GET("/users")
    void listUsers(Callback<List<User>> users);

    @GET("/users/{userId}")
    void getUser(@Path("userId") String userId, Callback<User> cb);

    @PUT("/users/{userId}")
    void updateUser(@Body User user, @Path("userId") String userId, Callback<User> cb);

    @POST("/register")
    void registerUser(@Body User user, Callback<User> cb);

    @POST("/login")
    void loginUser(@Query("username") String username, @Query("password") String password, Callback<User> user);

    // UserDigest

    // Game
    @GET("/games")
    void getGames(Callback<List<Game>> games);

    @GET("/games/{gameId}")
    void getGame(@Path("gameId") String gameId, Callback<Game> cb);

    // Round

    // Photo

    // Theme
    // TODO: Need to be able to pass arbitrary number of themes
}
