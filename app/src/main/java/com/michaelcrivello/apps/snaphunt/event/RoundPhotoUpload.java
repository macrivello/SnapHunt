package com.michaelcrivello.apps.snaphunt.event;

import java.io.File;

/**
 * Event posted on Bus for a photo submission in a round.
 */
public class RoundPhotoUpload {

    // TODO: Add other metadata
    File photo;

    public RoundPhotoUpload(File file){
        this.photo = file;
    }

    public File getPhoto() {
        return photo;
    }
}
