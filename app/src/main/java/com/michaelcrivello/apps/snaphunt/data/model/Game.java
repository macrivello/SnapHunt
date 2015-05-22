package com.michaelcrivello.apps.snaphunt.data.model;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 3/22/15.
 */
// TODO: ObjectID and Date de/serializer
// http://craigsmusings.com/2011/04/09/deserializing-mongodb-ids-and-dates-with-gson/

public class Game {
    ObjectId _id;
    int roundTimeLimit;
    int numberOfRounds;
    int currentRound;
    //TODO: currently Object Ids in model schema
    List<ObjectId> rounds;
    List<ObjectId> players; // players will be UserDigests
    Date timeCreated;
    Date timeLastModified;
    Date timeEnded;
    boolean gameOver;
    boolean gameStarted;

    public String getGameIdAsString() {
        return _id.toHexString();
    }

    public int getRoundTimeLimit() {
        return roundTimeLimit;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public List<ObjectId> getRounds() {
        return rounds;
    }

    public List<ObjectId> getPlayers() {
        return players;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public Date getTimeLastModified() {
        return timeLastModified;
    }

    public Date getTimeEnded() {
        return timeEnded;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
}
