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

import roboguice.inject.InjectView;

/**
 * Created by michael on 5/19/15.
 */
public class GameListView extends LinearLayout {
    Game game;

    ImageView gameIcon;
    TextView gameId;
    TextView gameStatus;

    public GameListView(Context context, Game game) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.game_list_item, this);

        gameIcon = (ImageView) findViewById(R.id.gameIcon);
        gameId = (TextView) findViewById(R.id.gameIdText);
        gameStatus = (TextView) findViewById(R.id.gameStatusText);

        this.setGame(game);
    }


    public void setGame(Game game) {
        this.game = game;
        gameId.setText(game.getGameIdAsString());
        gameStatus.setText(game.isGameStarted() ? "Game Started" : "Waiting to start");
        gameIcon.setImageResource(android.R.drawable.gallery_thumb);
    }

    public Game getGame() {
        return this.game;
    }
}
