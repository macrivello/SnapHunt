package com.michaelcrivello.apps.snaphunt.util;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.PersistableDownload;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.event.AWSTokenExpired;
import com.michaelcrivello.apps.snaphunt.event.S3PhotoDownload;
import com.michaelcrivello.apps.snaphunt.event.S3TransferManagerUpdated;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import roboguice.util.Ln;

/**
 * As of creation, custom RequestHandler for Picasso is in beta.
 */
public class S3RequestHandler extends RequestHandler {
    OkHttpClient okHttpClient;
    Bus bus;
    TransferManager transferManager;
    Context context;

    @Inject
    public S3RequestHandler(Context context, Bus bus, OkHttpClient okHttpClient){
        this.bus = bus;
        this.context = context;
        this.okHttpClient = okHttpClient;

        registerOnBus();
    }

    private void registerOnBus() {
        Ln.d("registerOnBus");
        bus.register(this);
    }

    private void unregisterOnBus() {
        bus.unregister(this);
    }

    @Override
    public boolean canHandleRequest(Request request) {
        // if request url is S3 and transferManager != null.
        List<String> pathSegments;

        if (request != null){
            Ln.d("Handing request with S3Handler");
            return request.uri.getHost().startsWith("s3-");
        }
        return false;
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        Ln.d("Load");
        String bucket = "", key = "";
        List<String> pathSegments = request.uri.getPathSegments();

        int i;
        for (i = 0; i < pathSegments.size(); i++) {
            String s = pathSegments.get(i);
            if (i == 0) {
                bucket = s;
            } else if (i == pathSegments.size() - 1){
                key = key.concat(s);
//                suffix = getFileExtension(s);
            } else {
                key = key.concat(s + "/");
            }
        }

        InputStream in = null;
        in = transferManager.getAmazonS3Client().getObject(bucket, key).getObjectContent();

        // if url is invalid, clear http cache (at least that call) and try again
        return new Result(in, Picasso.LoadedFrom.NETWORK);

    }

    private void removeRequestFromCache(Uri uri) {
        // TODO: JUST FLUSHING CACHE FOR NOW. PLEASE FIX THIS
        Ln.d("Clearing http cache");
        try {
            okHttpClient.getCache().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void updateTransferManager (S3TransferManagerUpdated transferManagerUpdated){
        Ln.d("updateTransferManager");
        this.transferManager = transferManagerUpdated.getTransferManager();
        AWSTokenExpired awsTokenExpired = transferManagerUpdated.getAwsTokenExpired();

        // Resume any pending downloads?
    }

    protected URL generatePreSignedUrl(String bucketName, String objectKey) {
        try {
            Ln.d("Generating signed url for " + bucketName + '/' + objectKey);
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey);

            Date d = new Date();
            d.setTime(d.getTime() + 86400000);
            generatePresignedUrlRequest.setExpiration(d);


            URL generatedURL = transferManager.getAmazonS3Client().generatePresignedUrl(generatePresignedUrlRequest);
            Ln.d("Generated SignedURL: " + generatedURL);
            return generatedURL;

        } catch (AmazonServiceException exception) {
            Ln.e("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            Ln.e("Error Message: " + exception.getMessage());
            Ln.e("HTTP  Code: " + exception.getStatusCode());
            Ln.e("AWS Error Code:" + exception.getErrorCode());
            Ln.e("Error Type:    " + exception.getErrorType());
            Ln.e("Request ID:    " + exception.getRequestId());
        } catch (AmazonClientException ace) {
            Ln.e("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            Ln.e("Error Message: " + ace.getMessage());
        }
        return null;
    }

}
