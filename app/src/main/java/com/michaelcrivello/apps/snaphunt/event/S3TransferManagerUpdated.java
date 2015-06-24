package com.michaelcrivello.apps.snaphunt.event;

import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;

/**
 * Created by tao on 6/21/15.
 */
public class S3TransferManagerUpdated {
    TransferManager transferManager;
    AWSTokenExpired awsTokenExpiredEvent;

    public S3TransferManagerUpdated(TransferManager transferManager, AWSTokenExpired awsTokenExpiredEvent) {
        this.transferManager = transferManager;
        this.awsTokenExpiredEvent = awsTokenExpiredEvent;
    }

    public TransferManager getTransferManager() {
        return transferManager;
    }

    public AWSTokenExpired getAwsTokenExpired() {
        return awsTokenExpiredEvent;
    }
}
