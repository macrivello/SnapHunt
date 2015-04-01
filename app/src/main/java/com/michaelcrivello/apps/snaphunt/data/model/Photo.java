package com.michaelcrivello.apps.snaphunt.data.model;

import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by michael on 3/22/15.
 */
public class Photo {
    ObjectId id;
    UserDigest owner;
    String url;
    String urlThumb;
    int size;
    int sizeThumb;
    String hash;
    String hashThumb;
    Theme theme;
    Date timeCreated;

    public ObjectId getId() {
        return id;
    }

    public UserDigest getOwner() {
        return owner;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlThumb() {
        return urlThumb;
    }

    public int getSize() {
        return size;
    }

    public int getSizeThumb() {
        return sizeThumb;
    }

    public String getHash() {
        return hash;
    }

    public String getHashThumb() {
        return hashThumb;
    }

    public Theme getTheme() {
        return theme;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }
}
