package com.michaelcrivello.apps.snaphunt.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.adapter.GameListAdapter;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.ui.GameActivity;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.view.GameListView;
import com.squareup.otto.Bus;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.util.Ln;
import rx.Observer;

/**
 * Created by michael on 5/20/15.
 */
public class GameList extends BaseFragment {
    @Inject SnaphuntApi snaphuntApi;
    ListView gamesListView;
    GameListAdapter gameListAdapter;
    ViewGroup emptyListOverlay;

    protected static final String TITLE = "Games";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameListAdapter = new GameListAdapter(SnaphuntApp.getInstance(), Collections.<Game>emptyList());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.games_list,container,false);
        gamesListView = (ListView) v.findViewById(R.id.games_list_view);
        emptyListOverlay = (ViewGroup) v.findViewById(R.id.empty_list_overlay);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gamesListView.setAdapter(gameListAdapter);
        setListListener();
        loadGamesList();
    }

    private void setListListener() {
        gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch new activity with Game
                GameListView selectedGameView = (GameListView) view;
                Game game = selectedGameView.getGame();
                String gameId = game.getGameIdAsString();

                // TODO: dont launch activities from fragments... communicate to parent activity
                // via interface method.
                Intent i = new Intent(getActivity(), GameActivity.class);
                i.putExtra(Constants.GAME_ID_KEY, gameId);

                startActivity(i);
            }
        });
    }

    private void loadGamesList() {
        // Get users games, load them into the adapter
        snaphuntApi.getGames(new Callback<List<Game>>() {
            @Override
            public void success(List<Game> games, Response response) {
                Ln.d("Loading %d games into list.", games.size());
                if (games.size() == 0) {
                    showEmptyListOverlay(true);
                } else {
                    showEmptyListOverlay(false);
                    gameListAdapter.setGames(games);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error loading list of games. " + error.getUrl() +  error.getResponse() + error.getBody());
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
