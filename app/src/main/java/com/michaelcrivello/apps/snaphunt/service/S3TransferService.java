package com.michaelcrivello.apps.snaphunt.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.event.RoundPhotoUpload;
import com.michaelcrivello.apps.snaphunt.event.S3UploadUpload;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.S3Util;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;

import needle.Needle;
import roboguice.service.RoboService;
import roboguice.util.Ln;

/**
 * Created by michael on 3/11/15.
 */
public class S3TransferService extends RoboService {
    @Inject Bus bus;
    private TransferManager transferManager;
    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Ln.d("Creating S3TransferService");
        super.onCreate();
        context = getApplicationContext();
        bus.register(this);
        initAwsServices();
    }

    private void initAwsServices() {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    transferManager = new TransferManager(S3Util.getCredProvider(context));
                } catch (AmazonClientException e) {
                    Ln.e("Error creating TransferManager. " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ln.d("S3TransferService starting.");

        return Service.START_STICKY;
    }


    /*
        Post events
     */
    public void postUpload(Upload upload, File file) {
        bus.post(new S3UploadUpload(upload, file));
    }

    /*
        Register events
     */

    @Subscribe
    public void onRoundPhotoUpload(RoundPhotoUpload roundPhotoUpload) {
        File file = roundPhotoUpload.getPhoto();
        if (file == null) {
            Ln.e("File to upload was null.");
            return;
        }

        Ln.d("onRoundPhotoUpload. File: " + file.getName());

        // TODO: add Util method to create s3 key.
        // userid-gameid-roundid-theme;
        Upload upload = transferManager.upload(Constants.BUCKET_NAME, Constants.PHOTO_UPLOAD_FOLDER + "/" + file.getName(), file);
        postUpload(upload, file);
    }
}
