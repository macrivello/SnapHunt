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
import com.michaelcrivello.apps.snaphunt.view.UserDigestListItemView;

import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;

/**
 * Simple adapter to bind UserDigest objects to simple ListView
 */
public class UserDigestAdapter extends BaseAdapter {
    private final Context context;
    List<UserDigest> userDigests;

    public UserDigestAdapter(Context context) {
        this.context = context;
        userDigests = Collections.EMPTY_LIST;
    }

    @Override
    public int getCount() {
        return userDigests.size();
    }

    @Override
    public UserDigest getItem(int position) {
        return userDigests != null ? userDigests.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //inflate view
        UserDigest userDigest = userDigests != null ? userDigests.get(position) : null;
        if (view == null) {
            view = new UserDigestListItemView(context);
            ((UserDigestListItemView)view).setUser(userDigest);
        }
        return view;
    }

    public void loadUsers(List<UserDigest> userDigests) {
        this.userDigests = userDigests != null ? userDigests : Collections.<UserDigest>emptyList();
        notifyDataSetChanged();
    }
}
