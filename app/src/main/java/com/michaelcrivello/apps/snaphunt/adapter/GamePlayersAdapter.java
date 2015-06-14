package com.michaelcrivello.apps.snaphunt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;

import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;

/**
 * Simple adapter to bind UserDigest objects to simple ListView
 */
public class GamePlayersAdapter extends BaseAdapter {
    private final Context context;
    List<ObjectId> userDigestIds;

    public GamePlayersAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return userDigestIds.size();
    }

    @Override
    public Object getItem(int position) {
        return userDigestIds != null ? userDigestIds.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //inflate view
        ObjectId userDigestId = userDigestIds != null ? userDigestIds.get(position) : null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gamePlayerView = inflater.inflate(R.layout.game_player_list_item, parent, false);
            TextView userDigestIdText = (TextView) gamePlayerView.findViewById(R.id.userDigestIdText);
            userDigestIdText.setText(userDigestId != null ? userDigestId.toHexString() : "invalid");

            return gamePlayerView;
        }
        return view;
    }

    public void loadGame(Game game) {
        userDigestIds = game != null ? game.getPlayers() : Collections.<ObjectId>emptyList();
    }
}
