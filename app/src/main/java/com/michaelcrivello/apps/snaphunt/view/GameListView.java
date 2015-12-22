package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.game.Game;
import com.michaelcrivello.apps.snaphunt.data.model.user.User;
import com.michaelcrivello.apps.snaphunt.data.model.user.UserDigest;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * Created by michael on 5/19/15.
 */
public class GameListView extends LinearLayout {
    @Inject SnaphuntApi snaphuntApi;
    Game game;

    ImageView gameIcon;
    TextView gameName;
    TextView gameStatus;
    TextView gamePlayers;

    @Inject
    public GameListView(Context context, Game game) {
        super(context);
        RoboGuice.injectMembers(context, this);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.game_list_item, this);

        gameIcon = (ImageView) findViewById(R.id.gameIcon);
        gameName = (TextView) findViewById(R.id.gameIdText);
        gameStatus = (TextView) findViewById(R.id.gameStatusText);
        gamePlayers = (TextView) findViewById(R.id.gamePlayersText);

        this.setGame(game);
        getPlayers();
    }


    public void setGame(Game game) {
        this.game = game;

        String gameName = "Game: " + game.getGameName();
        this.gameName.setText(gameName);
        gameStatus.setText(game.getState());
        gameIcon.setImageResource(R.drawable.ic_launcher);
    }

    private void getPlayers() {


        List<String> ids = Stream.of(game.getPlayers())
                .map(ObjectId::toHexString)
                .collect(Collectors.toList());


            snaphuntApi.listUsers(ids, new Callback<List<UserDigest>>() {
                @Override
                public void success(List<UserDigest> users, Response response) {
                    for (UserDigest ud : users){
                        gamePlayers.append(ud.getUsername() + '\n');
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Ln.e("Error populating game player list");
                }
            });
    }

    public Game getGame() {
        return this.game;
    }
}
