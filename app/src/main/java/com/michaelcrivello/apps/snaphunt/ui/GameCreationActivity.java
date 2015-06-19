package com.michaelcrivello.apps.snaphunt.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.adapter.SelectableUserDigestAdapter;
import com.michaelcrivello.apps.snaphunt.adapter.UserDigestAdapter;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.michaelcrivello.apps.snaphunt.misc.Selectable;
import com.michaelcrivello.apps.snaphunt.view.UserDigestListItemView;

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

    SelectableUserDigestAdapter selectableUserDigestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_create_activity);

        selectableUserDigestAdapter = new SelectableUserDigestAdapter(this);
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

                adapter.setSelected(item, !item.isSelected());
                itemView.setActivated(item.isSelected());

                Ln.d("user: " + (((UserDigestListItemView) view).getUser()).getUsername() + " selected: " + item.isSelected());
            }
        });
    }

    public void onStartGame(View v) {
        // Create game
        String message = "Users: ";
        for (Selectable selectable : (List<Selectable>)selectableUserDigestAdapter.getSelectedItems()) {
            message = message.concat(((UserDigest)selectable.getObject()).getUsername());
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
