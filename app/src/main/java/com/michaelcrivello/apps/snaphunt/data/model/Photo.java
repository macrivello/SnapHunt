package com.michaelcrivello.apps.snaphunt.data.model;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by michael on 3/22/15.
 */
public class Photo implements Serializable{
    ObjectId _id;
    ObjectId owner;
    String url;
    String urlThumb;
    int size;
    int sizeThumb;
    String hash;
    String hashThumb;
    ObjectId theme;
    Date timeCreated;

    public ObjectId getId() {
        return _id;
    }

    public ObjectId getOwner() {
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

    public ObjectId getTheme() {
        return theme;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public void setOwner(ObjectId owner) {
        this.owner = owner;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrlThumb(String urlThumb) {
        this.urlThumb = urlThumb;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSizeThumb(int sizeThumb) {
        this.sizeThumb = sizeThumb;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setHashThumb(String hashThumb) {
        this.hashThumb = hashThumb;
    }

    public void setTheme(ObjectId theme) {
        this.theme = theme;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }
}
