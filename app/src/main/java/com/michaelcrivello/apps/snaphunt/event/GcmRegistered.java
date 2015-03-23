package com.michaelcrivello.apps.snaphunt.event;

/**
 * Created by michael on 3/18/15.
 */
public class GcmRegistered {
    String regId;

    public GcmRegistered(String regId) {
        this.regId = regId;
    }

    public String getRegId() {
        return regId;
    }
}

