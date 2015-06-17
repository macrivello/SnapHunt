package com.michaelcrivello.apps.snaphunt.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.event.AWSTokenExpired;
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
import roboguice.util.RoboAsyncTask;

/**
 * Created by michael on 3/11/15.
 */
public class S3TransferService extends RoboService {
    @Inject Bus bus;
    private TransferManager transferManager;
    private CognitoCredentialsProvider sCredProvider;

    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Ln.d("Creating S3TransferService");
        super.onCreate();
        context = SnaphuntApp.getInstance();
        bus.register(this);
        initAwsServices();
    }

    private void initAwsServices() {
        if(sCredProvider == null) {
                sCredProvider = new CognitoCachingCredentialsProvider(
                        context,
                        Constants.COGNITO_POOL_ID,    /* Identity Pool ID */
                        Constants.AWS_REGION           /* Region */
                );
        }

        Needle.onBackgroundThread().serially().execute(new Runnable() {
            @Override
            public void run() {
                Ln.d("AWS Cred ID: " + sCredProvider.getIdentityId());

                transferManager = new TransferManager(sCredProvider);
            }
        });
    }

    private void refreshAwsCredentials() {
        if (sCredProvider != null) {
            Needle.onBackgroundThread().execute(new Runnable() {
                @Override
                public void run() {
                    sCredProvider.refresh();
                }
            });
        }
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

        // TODO: add Util method to create s3 key (filename).
        // userid-gameid-roundid-theme;
        Upload upload = null;
        try{
            upload = transferManager.upload(Constants.BUCKET_NAME, Constants.PHOTO_UPLOAD_FOLDER + "/" + file.getName(), file);
        } catch (AmazonClientException e) {
            Ln.e("Error uploading file: " +file.getName() + ". Error: " + e.getMessage());
        }
        postUpload(upload, file);
    }

    @Subscribe
    public void refreshToken(AWSTokenExpired awsTokenExpired) {
        refreshAwsCredentials();
        if(awsTokenExpired != null) {
            bus.post(awsTokenExpired.getPendingUpload());
        }
    }
}
