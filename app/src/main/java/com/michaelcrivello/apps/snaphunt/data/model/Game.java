package com.michaelcrivello.apps.snaphunt.data.model;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 3/22/15.
 */
// TODO: ObjectID and Date de/serializer
// http://craigsmusings.com/2011/04/09/deserializing-mongodb-ids-and-dates-with-gson/

public class Game {
    int roundTimeLimit;
    int numberOfRounds;
    int currentRound;
    //TODO: currently Object Ids in model schema
    List<Round> rounds;
    List<UserDigest> players;
    Date timeCreated;
    Date timeLastModified;
    Date timeEnded;
    boolean gameOver;
    boolean gameStarted;
}
