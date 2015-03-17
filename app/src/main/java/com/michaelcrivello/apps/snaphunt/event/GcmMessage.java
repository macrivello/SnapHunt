package com.michaelcrivello.apps.snaphunt.event;

/**
 * Created by miccrive on 3/16/15.
 */
public class GcmMessage {
    // TODO: Consider storing the User object in the GCM Message. There is 4k payload on a message,
    // that should be more than sufficient for the User model... especially if there is checks against
    // length of games and invitations, since those are the only arrays in the object model.
    String message;
    public GcmMessage (String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
