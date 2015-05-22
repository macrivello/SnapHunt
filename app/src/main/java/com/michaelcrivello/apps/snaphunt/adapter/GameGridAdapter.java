package com.michaelcrivello.apps.snaphunt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.michaelcrivello.apps.snaphunt.view.RoundInfoTileView;
import com.michaelcrivello.apps.snaphunt.view.UserTileView;

/**
 * Created by michael on 5/21/15.
 */
public class GameGridAdapter extends BaseAdapter {

    RoundInfoTileView roundInfoTileView;
    UserTileView thisUserTile;

    public void setRoundInfoTileView(RoundInfoTileView roundInfoTileView) {
        this.roundInfoTileView = roundInfoTileView;
    }

    public void setThisUserTile(UserTileView thisUserTile) {
        this.thisUserTile = thisUserTile;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (position == 0) {
            return this.roundInfoTileView;
        } else if (position == 1) {
            return this.thisUserTile;
        }
        return null;
    }
}
