package com.michaelcrivello.apps.snaphunt.data.model;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 3/22/15.
 *
 */


// Rounds always returned with populated fields?
public class Round {
    ObjectId _id;
    int roundNumber;
    boolean active;
    List<ObjectId> themes;
    ObjectId selectedTheme;
    ObjectId judge;
    ObjectId winner;
    ObjectId winningPhoto;
    Date timeCreated;
    Date timeLastModified;
    Date timeEnded;
    Date roundEnd;
    boolean allPhotosSubmitted;

    public ObjectId getId() {
        return _id;
    }

    public boolean isActive() {
        return active;
    }

    public List<ObjectId> getThemes() {
        return themes;
    }

    public ObjectId getSelectedTheme() {
        return selectedTheme;
    }

    public ObjectId getJudge() {
        return judge;
    }

    public ObjectId getWinner() {
        return winner;
    }

    public ObjectId getWinningPhoto() {
        return winningPhoto;
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

    public Date getRoundEnd() {
        return roundEnd;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public boolean isAllPhotosSubmitted() {
        return allPhotosSubmitted;
    }
}
