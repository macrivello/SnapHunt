package com.michaelcrivello.apps.snaphunt.event;

import java.io.File;

/**
 * Created by tao on 6/15/15.
 */
public class PhotoReadyForSubmit {
    File photo;

    public PhotoReadyForSubmit(File file){
        this.photo = file;
    }

    public File getFile() {
        return photo;
    }
}
