package com.michaelcrivello.apps.snaphunt.event;

import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;

import java.io.File;

/**
 * Created by miccrive on 3/14/15.
 */
public class S3Upload {
    Upload upload;
    File uploadedFile;
    String bucket, key;

    public S3Upload(Upload upload, File uploadedFile, String bucket, String key){
        this.upload = upload;
        this.uploadedFile = uploadedFile;
        this.bucket = bucket;
        this.key = key;
    }

    public Upload getUpload() {
        return upload;
    }

    public File getUploadedFile() {
        return uploadedFile;
    }

    public String getBucket() {
        return bucket;
    }

    public String getKey() {
        return key;
    }
}



