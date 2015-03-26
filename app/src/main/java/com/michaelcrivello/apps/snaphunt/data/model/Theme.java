package com.michaelcrivello.apps.snaphunt.data.model;

import org.bson.types.ObjectId;

/**
 * Created by michael on 3/22/15.
 */
public class Theme {
    ObjectId id;
    String phrase;

    // TODO: Update with an analytics object, or something to handle analytics better.
    int plays;
    int likes;
    int dislikes;
    int neutrals;
}
