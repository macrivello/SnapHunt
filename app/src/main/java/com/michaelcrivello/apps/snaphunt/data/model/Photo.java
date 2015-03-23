package com.michaelcrivello.apps.snaphunt.data.model;

import java.util.Date;

/**
 * Created by michael on 3/22/15.
 */
public class Photo {
    // TODO: currently ObjectId in schema
    UserDigest owner;
    String url;
    String urlThumb;
    long size;
    long sizeThumb;
    String hash;
    String hashThumb;
    Theme theme;
    Date timeCreated;
    Date timeLastModified;
}
