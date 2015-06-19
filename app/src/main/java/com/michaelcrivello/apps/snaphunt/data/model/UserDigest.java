package com.michaelcrivello.apps.snaphunt.data.model;

import org.bson.types.ObjectId;

/**
 * Created by michael on 3/22/15.
 */
public class UserDigest {
    ObjectId _id;
    String username;
    ObjectId userId;
    Photo profilePhoto;

    public ObjectId getId() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public Object getUserId() {
        return userId;
    }

    public Photo getProfilePhoto() {
        return profilePhoto;
    }
}
