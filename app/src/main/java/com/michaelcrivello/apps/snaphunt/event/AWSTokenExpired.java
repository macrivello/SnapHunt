package com.michaelcrivello.apps.snaphunt.event;

import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;

/**
 * Created by tao on 6/16/15.
 */
public class AWSTokenExpired {
    RoundPhotoUpload pendingUpload;

    public AWSTokenExpired(RoundPhotoUpload roundPhotoUpload) {
        pendingUpload = roundPhotoUpload;
    }

    public RoundPhotoUpload getPendingUpload() {
        return pendingUpload;
    }
}
