package com.michaelcrivello.apps.snaphunt.data.model;

/**
 * Created by michael on 3/22/15.
 */
public class UserDigest {
    String username;
    String id;
    Photo profilePhoto;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Photo getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Photo profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
