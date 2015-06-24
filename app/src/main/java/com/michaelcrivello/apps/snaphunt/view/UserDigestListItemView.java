package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.Game;
import com.michaelcrivello.apps.snaphunt.data.model.Photo;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.squareup.picasso.Picasso;

import org.bson.types.ObjectId;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * Created by tao on 6/18/15.
 */
public class UserDigestListItemView extends LinearLayout {
    private static Drawable DEFAULT_THUMB;
    @Inject SnaphuntApi snaphuntApi;
    @Inject Picasso picasso;
    UserDigest user;
    Context context;

    ImageView userThumb;
    TextView username;
    ViewGroup container;

    public UserDigestListItemView(Context context) {
        this(context, null);
    }

    public UserDigestListItemView(Context context, UserDigest user) {
        super(context);
        this.context = context;

        RoboGuice.injectMembers(context, this);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.user_digest_list_item, this);

        container = (ViewGroup) findViewById(R.id.listItemContainer);
        userThumb = (ImageView) findViewById(R.id.profilePhoto);
        username = (TextView) findViewById(R.id.usernameText);

        container.setBackground(getResources().getDrawable(R.drawable.list_item_selector, null));

        // getDrawable(id, theme);
        // TODO: update theme
        DEFAULT_THUMB = getResources().getDrawable(R.drawable.blank_avatar, null);

        if (user != null) {
            setUser(user);
        }
    }

    public void setUser(UserDigest user) {
        this.user = user;
        Photo photo = user.getProfilePhoto();
        picasso.load(photo != null ? photo.getUrl() : null)
                .placeholder(DEFAULT_THUMB)
               .into(userThumb);
        username.setText(user.getUsername());
    }

    public UserDigest getUser() {
        return user;
    }
}
