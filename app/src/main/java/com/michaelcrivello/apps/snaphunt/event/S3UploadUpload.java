package com.michaelcrivello.apps.snaphunt.event;

import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;

import java.io.File;

/**
 * Created by miccrive on 3/14/15.
 */
public class S3UploadUpload {
    Upload upload;
    File uploadedFile;

    public S3UploadUpload(Upload upload, File uploadedFile){
        this.upload = upload;
        this.uploadedFile = uploadedFile;
    }

    public Upload getUpload() {
        return upload;
    }

    public File getUploadedFile() {
        return uploadedFile;
    }
}



