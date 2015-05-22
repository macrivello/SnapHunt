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
    ObjectId id;
    int roundNumber;
    boolean active;
    List<Theme> themes;
    Theme selectedTheme;
    UserDigest judge;
    UserDigest winner;
    Photo winningPhoto;
    Date timeCreated;
    Date timeLastModified;
    Date timeEnded;
    Date roundEnd;
    boolean allPhotosSubmitted;

    public ObjectId getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public Theme getSelectedTheme() {
        return selectedTheme;
    }

    public UserDigest getJudge() {
        return judge;
    }

    public UserDigest getWinner() {
        return winner;
    }

    public Photo getWinningPhoto() {
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
