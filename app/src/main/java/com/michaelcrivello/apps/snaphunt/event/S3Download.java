package com.michaelcrivello.apps.snaphunt.event;

import com.amazonaws.mobileconnectors.s3.transfermanager.Download;

/**
 * Created by miccrive on 3/14/15.
 */
public class S3Download {
    Download download;

    public S3Download (Download download) {
        this.download = download;
    }

    public Download getDownload() {
        return download;
    }
}
