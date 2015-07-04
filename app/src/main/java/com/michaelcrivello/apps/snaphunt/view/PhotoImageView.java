package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.Photo;
import com.squareup.picasso.Picasso;

import org.bson.types.ObjectId;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * Created by tao on 6/29/15.
 */
public class PhotoImageView extends ImageView {
    @Inject SnaphuntApi snaphuntApi;
    @Inject Picasso picasso;
    String photoId;
    Photo photo;
    Context context;

    public PhotoImageView(Context context) {
        super(context);
        this.context = context;
        RoboGuice.injectMembers(context, this);
    }

    public PhotoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        RoboGuice.injectMembers(context, this);
    }

    public PhotoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        RoboGuice.injectMembers(context, this);
    }

    public PhotoImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        RoboGuice.injectMembers(context, this);

    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;

        picasso.load(photo.getUrl())
                .placeholder(R.drawable.logo_transparent)
                .into(this);
    }

    public void loadPhoto(String photoId) {
        // TODO: add progress drawable(?) as placeholder
        snaphuntApi.getPhoto(photoId, new Callback<Photo>() {
            @Override
            public void success(Photo photo, Response response) {
                setPhoto(photo);
            }

            @Override
            public void failure(RetrofitError error) {
                Ln.e("Error loading photo into image: " + error.getMessage());
            }
        });
    }
}
