package com.michaelcrivello.apps.snaphunt.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.UserDigestAdapter;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
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

    UserDigestAdapter userDigestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_create_activity);

        userDigestAdapter = new UserDigestAdapter(this);
        initPlayerList();
    }

    // TODO: This should load 'friends only'...?
    private void initPlayerList() {
        snaphuntApi.getUserDigestList(null, new Callback<List<UserDigest>>() {
            @Override
            public void success(List<UserDigest> userDigests, Response response) {
                userDigestAdapter.loadUsers(userDigests);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e(error, "Failed to load User List");
            }
        });
        inviteListView.setAdapter(userDigestAdapter);
    }

    public void onStartGame(View v) {
        // Create game
        Toast.makeText(this, "Start Game", Toast.LENGTH_LONG).show();
    }
}
