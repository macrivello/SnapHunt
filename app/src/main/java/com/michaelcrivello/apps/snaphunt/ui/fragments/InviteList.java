package com.michaelcrivello.apps.snaphunt.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.GameListAdapter;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.ui.GameActivity;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.view.GameListView;

import java.util.List;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.util.Ln;

/**
 * Created by michael on 5/20/15.
 */
public class InviteList extends BaseFragment {
    protected static final String TITLE = "Invites";
    @Inject SnaphuntApi snaphuntApi;
    ListView invitesListView;
    GameListAdapter inviteListAdapter;
    ViewGroup emptyListOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.invites_list,container,false);
        invitesListView = (ListView) root.findViewById(R.id.invites_list_view);
        emptyListOverlay = (ViewGroup) root.findViewById(R.id.empty_list_overlay);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        invitesListView.setAdapter(inviteListAdapter);
        setListListener();
        loadInvitesList();
    }

    private void setListListener() {
        invitesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch new activity with Game
                GameListView selectedGameView = (GameListView) view;
                Game game = selectedGameView.getGame();

                // TODO: dont launch activities from fragments... communicate to parent activity
                // via interface method.
                // TODO: Make network request to 'accept' invite
                Intent i = new Intent(getActivity(), GameActivity.class);
                i.putExtra(Constants.GAME_ID_KEY, game != null ? game.getGameIdAsString() : "");

                startActivity(i);
            }
        });
    }

    private void loadInvitesList() {
        // Get users games, load them into the adapter
        snaphuntApi.getInvites(new Callback<List<Game>>() {
            @Override
            public void success(List<Game> games, Response response) {
                Ln.d("Loading %d game_activity invites into list.", games.size());
                if (games.size() == 0) {
                    showEmptyListOverlay(true);
                } else {
                    showEmptyListOverlay(false);
                    inviteListAdapter.setGames(games);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error loading list of games.", error.getUrl(), error.getResponse(), error.getBody());
            }
        });
    }

    private void showEmptyListOverlay(boolean show) {
        emptyListOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
