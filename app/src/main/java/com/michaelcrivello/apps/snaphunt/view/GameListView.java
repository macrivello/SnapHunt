package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;

import org.bson.types.ObjectId;

import java.util.ArrayList;
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
    HashMap<ObjectId, UserDigest> usersMap;

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
        getPlayersDigest();
    }


    public void setGame(Game game) {
        this.game = game;
        String gameid = game.getGameIdAsString();
        String gameName = "Game: " + game.getGameName();
        this.gameName.setText(gameName);
        gameStatus.setText(game.isGameStarted() ? "Game Started" : "Waiting to start");
        gameIcon.setImageResource(R.drawable.ic_launcher);

    }

    private void getPlayersDigest() {
        List<String> ids = new ArrayList<>();
        for (ObjectId id : game.getPlayers()) {
            ids.add(id.toHexString());
        }
        snaphuntApi.getUserDigestList(ids, new Callback<List<UserDigest>>() {
            @Override
            public void success(List<UserDigest> userDigests, Response response) {
                updatePlayers(userDigests);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error populating game player list");
            }
        });
    }

    private void updatePlayers(List<UserDigest> userDigests) {
        usersMap = new HashMap<ObjectId, UserDigest>();
        for (UserDigest ud : userDigests) {
            Ln.d("adding user to playerslist: " + ud.getUsername());
            usersMap.put(ud.getId(), ud);
            gamePlayers.append(ud.getUsername() + '\n');

        }
    }

    public Game getGame() {
        return this.game;
    }
}
