package com.michaelcrivello.apps.snaphunt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.regions.Regions;

import org.w3c.dom.Text;

import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Created by michael on 3/11/15.
 * Sample Activity to test uploading photos to S3.
 */
public class SampleActivity extends Activity {

    @InjectView(R.id.selectPhotoText) TextView selectedPhoto;
    @InjectView(R.id.uploadUrlText) TextView uploadUrl;

    private static final int IMAGE_SELECTED_CODE = 69;
    TransferManager transferManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_photo_upload_activity);
        initAwsService();
    }

    private void initAwsService() {

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                SampleActivity.this     , // Context
                "us-east-1:4011341e-e994-4af4-97f5-7bf5f03e04bc", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        transferManager = new TransferManager(credentialsProvider);
    }


    /*
        Click handlers for buttons
     */
    public void uploadSelectedPhoto(View v) {
        launchGalleryImageSelector();
    }

    public void selectPhoto(View v) {

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
            selectedPhoto.setText("Selected Photo: " + uri.getPath());

        }
    }
}
