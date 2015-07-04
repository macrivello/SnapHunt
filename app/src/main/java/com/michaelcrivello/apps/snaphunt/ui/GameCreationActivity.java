package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.SelectableUserDigestAdapter;
import com.michaelcrivello.apps.snaphunt.adapter.UserDigestAdapter;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.michaelcrivello.apps.snaphunt.exception.TooManyItemsSelectedException;
import com.michaelcrivello.apps.snaphunt.misc.Selectable;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.view.UserDigestListItemView;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Query;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by tao on 6/17/15.
 */
public class GameCreationActivity extends BaseActivity {
    @InjectView(R.id.gameNameEdit) EditText gameName;
    @InjectView(R.id.numberOfRoundsEdit) EditText numberOfRounds;
    @InjectView(R.id.roundTimeLimitEdit) EditText roundTimeLimit;
    @InjectView(R.id.invitePlayerListView) ListView inviteListView;

    SelectableUserDigestAdapter selectableUserDigestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_create_activity);

        selectableUserDigestAdapter = new SelectableUserDigestAdapter(this, Constants.MAX_USERS_PER_GAME - 1);
        initPlayerList();
    }

    // TODO: This should load 'friends only'...?
    private void initPlayerList() {
        snaphuntApi.getUserDigestList(null, new Callback<List<UserDigest>>() {
            @Override
            public void success(List<UserDigest> userDigests, Response response) {
                selectableUserDigestAdapter.loadUsers(userDigests);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e(error, "Failed to load User List");
            }
        });
        inviteListView.setAdapter(selectableUserDigestAdapter);
        inviteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected ? Add view to list
                UserDigestListItemView itemView = (UserDigestListItemView) view;
                SelectableUserDigestAdapter adapter = ((SelectableUserDigestAdapter)parent.getAdapter());
                Selectable item = adapter.getSelectable(itemView.getUser().getId().toHexString());

                try {
                    adapter.setSelected(item, !item.isSelected());
                    itemView.setActivated(item.isSelected());
                    Ln.d("user: " + (((UserDigestListItemView) view).getUser()).getUsername() + " selected: " + item.isSelected());
                } catch (TooManyItemsSelectedException e) {
                    Ln.d("Too many items selected in list");
                    Toast.makeText(context, "Only " + adapter.getMaxSelection() + " Players Allowed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onStartGame(View v) {
        // Create game
        List<String> players = new ArrayList<>();
        for (Selectable selectable : (List<Selectable>)selectableUserDigestAdapter.getSelectedItems()) {
            players.add(((UserDigest) selectable.getObject()).getId().toHexString());
        }

        // Create Game. Route to Game Activity
        //TODO: Builder pattern for larger objects like Game?
        Game newGame = new Game();
        newGame.setGameName(gameName.getText().toString().trim());
        newGame.setNumberOfRounds(Constants.NUMBER_OR_ROUNDS_DEFAULT);
        newGame.setRoundTimeLimit(Constants.ROUND_TIME_LIMIT);

        snaphuntApi.createGame(newGame, players, new Callback<Game>() {
            @Override
            public void success(Game game, Response response) {
                // TODO: Where should the new game object get stored? Does it need to be?

                Ln.d("Starting new game");
                Intent i = new Intent(GameCreationActivity.this, GameActivity.class);
                i.putExtra(Constants.GAME_KEY, game);
                startActivity(i);
                overridePendingTransition(0, 0);

                // Remove only this activity from stack
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error starting new game", error);
            }
        });
    }
}
