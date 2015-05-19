package com.michaelcrivello.apps.snaphunt.data.model;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * Created by miccrive on 3/16/15.
 */
public class User {
    ObjectId _id;
    String email;
    String username;
    String authToken;
    String gcmRegId;
    String profilePhoto;
    String password;
    String provider;
    String providerId;
    Object providerData;
    List<Game> games;
    List<Game> invitations;
    Date timeCreated;
    Date timeLastModifed;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ObjectId getId() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getPassword() {
        return password;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public Object getProviderData() {
        return providerData;
    }

    public List<Game> getGames() {
        return games;
    }

    public List<Game> getInvitations() {
        return invitations;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public Date getTimeLastModifed() {
        return timeLastModifed;
    }
}

