package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;

import org.bson.types.ObjectId;

import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by michael on 5/19/15.
 */
public class GameListView extends LinearLayout {
    Game game;

    ImageView gameIcon;
    TextView gameId;
    TextView gameStatus;
    TextView gamePlayers;

    public GameListView(Context context, Game game) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.game_list_item, this);

        gameIcon = (ImageView) findViewById(R.id.gameIcon);
        gameId = (TextView) findViewById(R.id.gameIdText);
        gameStatus = (TextView) findViewById(R.id.gameStatusText);
        gamePlayers = (TextView) findViewById(R.id.gamePlayersText);

        this.setGame(game);
    }


    public void setGame(Game game) {
        this.game = game;
        gameId.setText(game.getGameIdAsString());
        gameStatus.setText(game.isGameStarted() ? "Game Started" : "Waiting to start");
        gameIcon.setImageResource(R.drawable.ic_launcher);

        // TODO: This will need to ethier switch back to UserDigests or make additional network requests, thus routes for users/userdigest
        List<ObjectId> users = game.getPlayers();
        if (users == null || users.size() < 1) {
            gamePlayers.setText("No Players in this game_activity.");
        } else {
            for (ObjectId u : users){
                gamePlayers.append(u.toHexString());
            }
        }
    }

    public Game getGame() {
        return this.game;
    }
}
