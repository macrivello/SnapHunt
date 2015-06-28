package com.michaelcrivello.apps.snaphunt.data.model;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Game model.
 */
// http://craigsmusings.com/2011/04/09/deserializing-mongodb-ids-and-dates-with-gson/

public class Game implements Serializable {
    static final long serialVersionUID = 44L;


    ObjectId _id;
    String gameName;
    int roundTimeLimit;
    int numberOfRounds;
    int currentRound;
    List<ObjectId> rounds;
    // Setting players as ObjectIds temporarily since mongoose deep populate is not populating user.games.players
    List<ObjectId> players;
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

    public void setRoundTimeLimit(int roundTimeLimit) {
        this.roundTimeLimit = roundTimeLimit;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public void setPlayers(List<ObjectId> players) {
        this.players = players;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
