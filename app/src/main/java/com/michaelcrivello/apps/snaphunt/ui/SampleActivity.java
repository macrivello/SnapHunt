package com.michaelcrivello.apps.snaphunt.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.model.Bucket;
import com.michaelcrivello.apps.snaphunt.R;

import java.io.File;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * Created by michael on 3/11/15.
 * Sample Activity to test uploading photos to S3.
 */
public class SampleActivity extends RoboActivity {

    private static final String SH_BUCKET_NAME = "snaphunt-storage";
    private final String TAG = SampleActivity.this.getClass().getCanonicalName();
    @InjectView(R.id.selectPhotoText) TextView selectedPhotoText;
    @InjectView(R.id.uploadUrlText) TextView uploadUrlText;

    private static final int IMAGE_SELECTED_CODE = 69;
    TransferManager transferManager;
    private File selectedPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_photo_upload_activity);
        initAwsService();
    }

    private void initAwsService() {

        transferManager = new TransferManager();
//        new ListMyBuckets().execute();
    }

    private class ListMyBuckets extends AsyncTask<Object, Void, List<Bucket>> {

        @Override
        protected List<Bucket> doInBackground(Object... params) {
            return transferManager.getAmazonS3Client().listBuckets();
        }

        @Override
        protected void onPostExecute(List<Bucket> result) {
            for (Bucket b: result){
                Log.d(TAG, "bucket: " + b.getName());
            }
        }
    }    /*
        Click handlers for buttons
     */
    public void uploadSelectedPhoto(View v) {
        Log.d(TAG, "Attempting to upload file.");
        if (transferManager != null && selectedPhotoFile != null) {


            Upload upload = transferManager.upload(SH_BUCKET_NAME, selectedPhotoFile.getName(), selectedPhotoFile);
            upload.addProgressListener(new ProgressListener() {
                @Override
                public void progressChanged(ProgressEvent progressEvent) {
                    switch (progressEvent.getEventCode()) {
                        case ProgressEvent.COMPLETED_EVENT_CODE:
                            Log.d(TAG, "Upload COMPLETED_EVENT_CODE.");
                            break;
                        case ProgressEvent.FAILED_EVENT_CODE:
                            Log.d(TAG, "Upload FAILED_EVENT_CODE.");
                            break;
                        case ProgressEvent.STARTED_EVENT_CODE:
                            Log.d(TAG, "Upload STARTED_EVENT_CODE.");
                            break;
                        case ProgressEvent.CANCELED_EVENT_CODE:
                            Log.d(TAG, "Upload CANCELED_EVENT_CODE.");
                            break;
                        case ProgressEvent.PREPARING_EVENT_CODE:
                            Log.d(TAG, "Upload PREPARING_EVENT_CODE.");
                            break;
                        case ProgressEvent.RESET_EVENT_CODE:
                            Log.d(TAG, "Upload RESET_EVENT_CODE.");
                            break;
                        case ProgressEvent.PART_COMPLETED_EVENT_CODE:
                            Log.d(TAG, "Upload PART_COMPLETED_EVENT_CODE.");
                            break;
                        case ProgressEvent.PART_FAILED_EVENT_CODE:
                            Log.d(TAG, "Upload PART_FAILED_EVENT_CODE.");
                            break;
                        case ProgressEvent.PART_STARTED_EVENT_CODE:
                            Log.d(TAG, "Upload PART_STARTED_EVENT_CODE.");
                            break;
                    }
                }
            });
        }
    }

    public void selectPhoto(View v) {
        launchGalleryImageSelector();
    }


    private void launchGalleryImageSelector() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Photo"), IMAGE_SELECTED_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_SELECTED_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();
            selectedPhotoFile = new File(uri.getPath());

            selectedPhotoText.setText("Selected Photo: " + uri.getPath().toString());

        } else {
            selectedPhotoFile = null;
        }
    }
}
