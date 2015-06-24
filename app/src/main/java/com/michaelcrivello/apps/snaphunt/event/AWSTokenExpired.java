package com.michaelcrivello.apps.snaphunt.event;

/**
 * Created by tao on 6/16/15.
 */
public class AWSTokenExpired {
    S3PhotoUpload pendingUpload;
    S3PhotoDownload pendingDownload;

    public AWSTokenExpired(S3PhotoUpload s3PhotoUpload, S3PhotoDownload photoDownload) {
        pendingUpload = s3PhotoUpload;
        pendingDownload = photoDownload;
    }

    public S3PhotoUpload getPendingUpload() {
        return pendingUpload;
    }

    public S3PhotoDownload getPendingDownload() {
        return pendingDownload;
    }
}
