package com.michaelcrivello.apps.snaphunt.ui;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.GameGridAdapter;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.Round;
import com.michaelcrivello.apps.snaphunt.data.model.Theme;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.michaelcrivello.apps.snaphunt.ui.fragments.ThemeSelection;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.view.RoundInfoTileView;
import com.michaelcrivello.apps.snaphunt.view.UserTileView;
import com.squareup.otto.Bus;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.util.Ln;

/**
 * Created by michael on 3/24/15.
 */
public class GameActivity extends BaseActivity implements ThemeSelection{
    protected Game game;
    protected Round currentRound;
    protected User currentJudge;
    protected List<UserDigest> players;
    protected Theme currentTheme;

    AbsListView gameGridView; // GridView
    GameGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        String gameId;
        Bundle b;
        if ((b = getIntent().getExtras()) != null) {
            gameId = b.getString(Constants.GAME_ID_KEY, "");

            if (!gameId.isEmpty()) {
                getGameData(gameId);
            }
        }

        initGameGrid();
        gameStateCheck();
    }

    // TODO: Check if user is Judge. Check if theme has been selected, if not then show overlay.
    private void gameStateCheck() {

    }

    private void initGameGrid() {
        gameGridView = (AbsListView) findViewById(R.id.game_grid);
        adapter = new GameGridAdapter();

        RoundInfoTileView roundInfoTileView = new RoundInfoTileView(this);

        UserTileView userTileView = new UserTileView(this);
        userTileView.setUsernameText(userManager.getUser().getUsername());

        adapter.setRoundInfoTileView(roundInfoTileView);
        adapter.setThisUserTile(userTileView);
        gameGridView.setAdapter(adapter);
    }


    @Override
    public void themeSelected(Theme theme) {
        // Make a network request to update the game on the server that this rounds theme
        // has been selected. This will send push events to the players in the game.

        // The Round object itself needs to be updated too. Round.selectedTheme.

        // Remove the overlay fragment

        // Update the Game status square with the theme.
    }

    public void getGameData(final String gameId) {
        snaphuntApi.getGame(gameId, new Callback<Game>() {
            @Override
            public void success(Game game, Response response) {
                Ln.d("Loading Game data for gameId: " + game.getGameIdAsString());
                loadGameData(game);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error downloading Game object for gameId: " + gameId);

            }
        });
    }

    private void loadGameData(Game game) {
        this.game = game;

        // Load player tiles
    }
}
