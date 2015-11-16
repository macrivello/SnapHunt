package com.michaelcrivello.apps.snaphunt.data.model;


import android.databinding.BindingConversion;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * User model. Base object that represents a user of Snaphunt.
 */
public class User {
    ObjectId _id;
    ObjectId userDigest;
    String email;
    String username;
    String authToken;
    String gcmRegId;
    ObjectId profilePhoto;
    String password;
    String provider;
    String providerId;
    Object providerData;
    List<ObjectId> games;
    List<ObjectId> invitations;
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

    public void setProfilePhoto(ObjectId profilePhoto) {
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

    public ObjectId getProfilePhoto() {
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

    public List<ObjectId> getGames() {
        return games;
    }

    public List<ObjectId> getInvitations() {
        return invitations;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public Date getTimeLastModifed() {
        return timeLastModifed;
    }

    public ObjectId getUserDigest() {
        return userDigest;
    }

    @BindingConversion
    public static String convertObjectIdToString(ObjectId objectId){
        return objectId.toHexString();
    }
}

