package com.michaelcrivello.apps.snaphunt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.GamePlayersAdapter;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.Round;
import com.michaelcrivello.apps.snaphunt.data.model.Theme;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.michaelcrivello.apps.snaphunt.ui.fragments.ThemeSelection;
import com.michaelcrivello.apps.snaphunt.util.Constants;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by michael on 3/24/15.
 */
public class GameActivity extends BaseActivity implements ThemeSelection {
    @InjectView(R.id.gameOpenCameraButton) Button takePhotoButton;
    @InjectView(R.id.gameSubmitPhotoButton) Button submitPhotoButton;
    @InjectView(R.id.gamePlayersListView) ListView playersListView;
    @InjectView(R.id.gameRoundStatusText) TextView roundStatusText;
    @InjectView(R.id.gameThemeText) TextView themeText;
    @InjectView(R.id.gameRoundNumberText) TextView roundNumberText;

    protected Game game;
    protected Round currentRound;
    protected User currentJudge;
    protected List<UserDigest> players;
    protected Theme currentTheme;

    protected GamePlayersAdapter gamePlayersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        getGameData(getGameIdFromIntent());

        gamePlayersAdapter = new GamePlayersAdapter(this);
    }

    private void loadGameData(Game game) {
        this.game = game;
        this.currentRound = game.getRounds().get(game.getCurrentRound());
        this.currentTheme = currentRound.getSelectedTheme();

        gameStateCheck();

        // set TextViews
        roundStatusText.setText(currentRound.isActive() ? "Started" : "Not Started");
        String themeStr;
        if (currentTheme != null) {
            themeStr = "Theme: " + currentTheme.getPhrase();
        } else {
            themeStr = "No Theme Selected";
        }

        themeText.setText(themeStr);
        roundNumberText.setText("Current Round: " + game.getCurrentRound());

        // Add Header
        View header = getLayoutInflater().inflate(R.layout.listview_header, playersListView, false);
        TextView headerText = (TextView) header.findViewById(R.id.listViewHeaderText);
        headerText.setText("Players");

        playersListView.addHeaderView(header, null, false);

        // Setup Adapter
        gamePlayersAdapter.loadGame(game);
        playersListView.setAdapter(gamePlayersAdapter);

    }

    // Check the game_activity state and handle appropriately, such as prompting Theme selection.
    private void gameStateCheck() {
        // TODO: Check if user is Judge. Check if theme has been selected, if not then show overlay.
    }


    @Override
    public void themeSelected(Theme theme) {
        // Make a network request to update the game_activity on the server that this rounds theme
        // has been selected. This will send push events to the players in the game_activity.

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



    //TODO refactor, too verbose
    public String getGameIdFromIntent() {
        String gameId = null;
        Bundle b;
        if ((b = getIntent().getExtras()) != null) {
            gameId = b.getString(Constants.GAME_ID_KEY, "");

            if (!gameId.isEmpty()) {
                Ln.d("Recieved gameId from intent: " + gameId);
            } else {
                // No game_activity data passed to Activity. Route back to HomePage for now.
                // TODO: Handle an empty gameId
                Ln.e("Empty gameId recieved from intent.");
            }
        }

        return gameId;
    }

    public void onTakePhotoClick(View v) {
        // Grab code from sample activity.
    }

   public void onSubmitPhotoClick(View v) {
       // Submit photo
   }
}
