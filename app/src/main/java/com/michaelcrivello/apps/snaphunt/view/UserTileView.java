package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.model.Round;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;

/**
 * Created by michael on 5/21/15.
 */
public class UserTileView extends RelativeLayout {
    UserDigest user;
    TextView usernameText, gamePointsText;
    ImageView userTileThumbnail;

    public UserTileView(Context context) {
        this(context, null);
    }

    public UserTileView(Context context, UserDigest user) {
        super(context);
        this.user = user;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.user_tile, this, true);

        usernameText = (TextView) findViewById(R.id.usernameText);
        gamePointsText = (TextView) findViewById(R.id.gamePointsText);
        userTileThumbnail = (ImageView) findViewById(R.id.userTileThumbnail);

        setUserInfo();
    }

    private void setUserInfo() {
        if (user != null) {
            usernameText.setText(user.getUsername());

            // TODO: finish updating this shit
        }
    }

    public void setUsernameText(String username) {
        usernameText.setText(username);
    }

}
