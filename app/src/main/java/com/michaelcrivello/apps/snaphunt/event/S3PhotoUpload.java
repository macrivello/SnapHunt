package com.michaelcrivello.apps.snaphunt.event;

import java.io.File;

/**
 * Event posted on Bus for a photo submission in a round.
 */
public class S3PhotoUpload {

    // TODO: Add other metadata
    File photo;

    public S3PhotoUpload(File file){
        this.photo = file;
    }

    public File getPhoto() {
        return photo;
    }
}
