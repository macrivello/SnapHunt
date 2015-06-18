package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
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

    public UserDigestListItemView(Context context) {
        super(context);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.user_digest_list_item, this);

        userThumb = (ImageView) findViewById(R.id.profilePhoto);
        username = (TextView) findViewById(R.id.usernameText);

        // getDrawable(id, theme);
        // TODO: update theme
        DEFAULT_THUMB = getResources().getDrawable(R.drawable.blank_avatar, null);
    }

    public void setUser(UserDigest user) {
        Photo photo = user.getProfilePhoto();
        Picasso.with(context)
                .load(photo != null ? photo.getUrl() : null)
                .placeholder(DEFAULT_THUMB)
                .into(userThumb);
        username.setText(user.getUsername());
    }

}
