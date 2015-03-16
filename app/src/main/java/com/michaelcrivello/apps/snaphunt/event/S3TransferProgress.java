package com.michaelcrivello.apps.snaphunt.event;

import com.amazonaws.mobileconnectors.s3.transfermanager.TransferProgress;
import com.michaelcrivello.apps.snaphunt.service.S3TransferService;

/**
 * Created by miccrive on 3/14/15.
 */
public class S3TransferProgress {
    String transferDescription;
    TransferProgress transferProgress;

    public S3TransferProgress(String transferDescription, TransferProgress transferProgress) {
        this.transferDescription = transferDescription;
        this.transferProgress = transferProgress;
    }

    public String getTransferDescription() {
        return transferDescription;
    }

    public TransferProgress getTransferProgress() {
        return transferProgress;
    }
}
