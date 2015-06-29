package com.michaelcrivello.apps.snaphunt.data.api;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.Round;
import com.michaelcrivello.apps.snaphunt.data.model.Theme;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
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
    void loginUser(@Query("username") String username, @Query("password") String password, Callback<User> cb);

    // UserDigest
    @GET("/userdigest/:id")
    void getUserDigest(@Path("id") String userId, Callback<UserDigest> cb);

    @GET("/userdigest")
    void getUserDigestList(@Query("id") List<String> userDigestIds, Callback<List<UserDigest>> cb);

    // Game
    @GET("/games")
    void getGames(Callback<List<Game>> games);

    @POST("/games")
    void createGame(@Body Game newGame, @Query("id") List<String> userDigestIds, Callback<Game> cb);

    @GET("/games/{gameId}")
    void getGame(@Path("gameId") String gameId, Callback<Game> cb);

    @GET("/invites")
    void getInvites(Callback<List<Game>> callback);

    @GET("/invites/{gameId}")
    void getInvite(@Path("gameId") String gameId, Callback<Game> cb);

    @GET("/invites/{gameId}/accept")
    void acceptInvite(@Path("gameId") String gameId, Callback<Game> cb);

    // Round
    @GET("/games/{gameId}/rounds/{roundId}")
    void getRound(@Path("gameId") String gameId, @Path("roundId")String roundId, Callback<Round> cb);

    @PUT("/games/{gameId}/rounds/{roundId}")
    void setThemeForRound(@Path("gameId") String gameId, @Path("roundId")String roundId, Callback<Theme> cb);

    // Photo

    // Theme
    @GET("/games/{gameId}/rounds/{roundId}/themes")
    void getThemes(@Path("gameId") String gameId, @Path("roundId")String roundId, Callback<List<Theme>> cb);

    @GET("/games/{gameId}/rounds/{roundId}/themes/{themeId}")
    void getTheme(@Path("gameId") String gameId, @Path("roundId")String roundId, @Path("themeId")String themeId, Callback<Theme> cb);

    @POST("/games/{gameId}/rounds/{roundId}/themes/{themeId}")
    void selectTheme(@Path("gameId") String gameId, @Path("roundId")String roundId, @Path("themeId") String themeId, Callback<Round> cb);
}
