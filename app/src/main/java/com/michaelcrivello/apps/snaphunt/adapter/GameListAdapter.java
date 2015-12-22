package com.michaelcrivello.apps.snaphunt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.michaelcrivello.apps.snaphunt.data.model.game.Game;
import com.michaelcrivello.apps.snaphunt.data.model.user.User;
import com.michaelcrivello.apps.snaphunt.view.GameListView;

import java.util.List;

/**
 * Created by michael on 5/19/15.
 */
public class GameListAdapter extends BaseAdapter {
    private Context context;
    private User user;
    private List<Game> games;

    public GameListAdapter(Context context, List<Game> games){
        this.context = context;
        this.games = games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int i) {
        return games.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // TODO: Implement ViewHolder pattern. just getting things working now.
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        GameListView gameListView = null;

        if (view == null) {
            gameListView = new GameListView(this.context, this.games.get(i));
        }
        else {
            gameListView = (GameListView)view;
        }
        gameListView.setGame(this.games.get(i));
        return gameListView;
    }
}
