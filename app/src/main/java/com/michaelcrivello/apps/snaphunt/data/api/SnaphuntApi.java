package com.michaelcrivello.apps.snaphunt.data.api;

import com.michaelcrivello.apps.snaphunt.data.model.game.Game;
import com.michaelcrivello.apps.snaphunt.data.model.Photo;
import com.michaelcrivello.apps.snaphunt.data.model.round.Round;
import com.michaelcrivello.apps.snaphunt.data.model.Theme;
import com.michaelcrivello.apps.snaphunt.data.model.user.User;
import com.michaelcrivello.apps.snaphunt.data.model.user.UserDigest;
import com.michaelcrivello.apps.snaphunt.util.GsonUtil;

import java.util.List;

import retrofit.Callback;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
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

    // DigitalOcean Server. 'snaphunt' droplet.
    String API_ENDPOINT = "https://snaphuntapp.co/api/" + API_VERSION + "/";

    // 10.0.2.2 is this machine localhost http://developer.android.com/tools/devices/emulator.html#networkaddresses
    String API_ENDPOINT_EMULATOR_LOCAL = "http://10.0.2.2:3000/api/" + API_VERSION + "/";

    String API_ENDPOINT_LOCAL = "http://192.168.1.89:3000/api/" + API_VERSION + "/";

    String DATA_FORM = "type";
    String FULL_FORM = "full";
    String DIGEST_FORM = "digest";
    String POPULATED_FORM = "populated";

    // TODO: Implement API to return Observables

    // User
    @GET("/user")
    void getUserFromAuthToken(Callback<User> user);

    @GET("/users")
    void listUsers(Callback<List<UserDigest>> users);

    @GET("/users")
    void listUsers(@Query("id") List<String> userIds, Callback<List<UserDigest>> users);

    @GET("/users/{userId}")
    void getUser(@Path("userId") String userId, Callback<UserDigest> cb);

    @PUT("/users/{userId}")
    void updateUser(@Body User user, @Path("userId") String userId, Callback<User> cb);

    @POST("/register")
    void registerUser(@Body User user, Callback<User> cb);

    @GET("/login")
    void loginUser(@Query("username") String username, @Query("password") String password, Callback<User> cb);

    // Game
    @GET("/games")
    void getGames(Callback<List<Game>> games);

    @POST("/games")
    void createGame(@Body Game newGame, @Query("id") List<String> userIds, Callback<Game> cb);

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
    @GET("/photo/{photoId}")
    void getPhoto(@Path("photoId")String photoId, Callback<Photo> cb);

    @GET("/games/{gameId}/rounds/{roundId}/photo/")
    void getPhotoFromUserId(@Path("gameId") String gameId, @Path("roundId") String roundId, @Query("uid") String userId, Callback<Photo> cb);

    @GET("/games/{gameId}/rounds/{roundId}/photo/{photoId}/winner")
    void submitWinner(@Path("gameId") String gameId, @Path("roundId")String roundId, @Path("photoId")String photoId, Callback<Round> cb);

    @POST("/games/{gameId}/rounds/{roundId}/photo")
    void submitPhoto(@Body Photo photo, @Path("gameId") String gameId, @Path("roundId")String roundId, Callback<Photo> cb);

    // Theme
    @GET("/games/{gameId}/rounds/{roundId}/themes")
    void getThemes(@Path("gameId") String gameId, @Path("roundId")String roundId, Callback<List<Theme>> cb);

    @GET("/games/{gameId}/rounds/{roundId}/themes/{themeId}")
    void getTheme(@Path("gameId") String gameId, @Path("roundId")String roundId, @Path("themeId")String themeId, Callback<Theme> cb);

    @GET("/games/{gameId}/rounds/{roundId}/themes/{themeId}")
    void selectTheme(@Path("gameId") String gameId, @Path("roundId")String roundId, @Path("themeId") String themeId, Callback<Round> cb);
}
