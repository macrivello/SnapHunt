package com.michaelcrivello.apps.snaphunt.event;

/**
 * Created by tao on 6/23/15.
 */
public class S3PhotoDownload {
    String bucket;
    String key;

    public S3PhotoDownload(String bucket, String key) {
        this.bucket = bucket;
        this.key = key;
    }

    public String getBucket() {
        return bucket;
    }

    public String getKey() {
        return key;
    }
}
