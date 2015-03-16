package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.event.RoundPhotoUpload;
import com.michaelcrivello.apps.snaphunt.event.S3UploadUpload;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by michael on 3/11/15.
 * Sample Activity to test uploading photos to S3.
 */
public class SampleActivity extends RoboActivity {
    @InjectView(R.id.selectPhotoText) TextView selectedPhotoText;
    @InjectView(R.id.uploadUrlText) TextView uploadUrlText;
    @InjectView(R.id.progress_wheel) ProgressWheel progressWheel;
    @InjectView(R.id.photoPreview) ImageView photoPreview;
    @Inject Bus bus;

    private static final int IMAGE_SELECTED_CODE = 69;
    private static final int REQUEST_IMAGE_CAPTURE = 70;
    private static final  int PLAY_SERVICES_RESOLUTION_REQUEST = 71;

    private String selectedPhotoFilePath;
    private File selectedPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_photo_upload_activity);

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
//        if (checkPlayServices()) {
//            gcm = GoogleCloudMessaging.getInstance(this);
//            regid = getRegistrationId(context);
//
//            if (regid.isEmpty()) {
//                registerInBackground();
//            }
//        } else {
//            Log.i(TAG, "No valid Google Play Services APK found.");
//        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Ln.e("Device is not supported");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    /*
        Click handlers for buttons
     */
    public void uploadSelectedPhoto(View v) {
        Ln.d("Attempting to upload file.");

        // Upload progress is returned as S3TransferProgress event
        bus.post(new RoundPhotoUpload(selectedPhotoFile));
    }

    public void selectPhoto(View v) {
        launchGalleryImageSelector();
    }

    public void takePhoto(View v) {
        launchCamera();
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            selectedPhotoFile = null;
            try {
                selectedPhotoFile = createImageFile();
            } catch (IOException ex) {
                selectedPhotoFile = null;
                Ln.e(ex.getMessage());
            }

            if (selectedPhotoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(selectedPhotoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
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

        switch (requestCode) {
            case IMAGE_SELECTED_CODE:
                if (resultCode != RESULT_OK || data.getData() == null) {
                    Ln.d("No image selected");
                    return;
                }

                handleImageSelected(data);
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode != RESULT_OK) {
                    Ln.d("No photo taken");
                    return;
                }

                handleCameraResult(data);
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalCacheDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        selectedPhotoFilePath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void handleCameraResult(Intent data) {
        if (selectedPhotoFile != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(selectedPhotoFile.getAbsolutePath());
            photoPreview.setImageBitmap(bitmap);
            selectedPhotoText.setText("Selected Photo: " + selectedPhotoFile.getName());
        }

    }

    private void handleImageSelected(Intent data) {
        selectedPhotoFile = null;

        Uri imageUri = data.getData();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            selectedPhotoText.setText("Selected Photo: " + imageUri.getPath());
            photoPreview.setImageBitmap(bitmap);


            selectedPhotoFile = null;
            try {
                selectedPhotoFile = createImageFile();
                writeBitmapToFile(bitmap, selectedPhotoFile);
            } catch (Exception e) {
                selectedPhotoFile = null;
                e.printStackTrace();
                Ln.e(e.getMessage());
            }
        }
    }

    private void writeBitmapToFile(Bitmap bitmap, File selectedPhotoFile) throws Exception{
        OutputStream os;
        os = new FileOutputStream(selectedPhotoFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.flush();
        os.close();
    }

    @Subscribe
    public void onS3Upload(S3UploadUpload s3UploadUpload) {
        Ln.d("onS3Upload");
        Upload upload = s3UploadUpload.getUpload();
        File file = s3UploadUpload.getUploadedFile();


        upload.addProgressListener(new S3UploadProgressListener(upload, file));
    }


    private class S3UploadProgressListener implements ProgressListener {
        protected Upload upload;
        protected File file;

        public S3UploadProgressListener(Upload upload, File file) {
            this.upload = upload;
            this.file = file;
        }

        @Override
        public void progressChanged(final ProgressEvent progressEvent) {
            if (upload == null) return;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int percentTransfered = ((int)upload.getProgress().getPercentTransferred());
                    if (percentTransfered > 0){
                        if (progressWheel.getVisibility() == View.INVISIBLE) {
                            progressWheel.setVisibility(View.VISIBLE);
                        }
                        progressWheel.setProgress(percentTransfered);
                    }

                    switch (progressEvent.getEventCode()) {
                        case ProgressEvent.COMPLETED_EVENT_CODE:
                            Ln.d("Upload Complete: " + upload.getDescription());
                            progressWheel.setProgress(100);
                            uploadUrlText.setText(upload.getDescription());
                            file.delete();
                            break;
                        case ProgressEvent.FAILED_EVENT_CODE:
                            try {
                                AmazonClientException e = upload.waitForException();
                                Ln.e("Unable to upload file to Amazon S3: " + e.getMessage());
                            } catch (InterruptedException e) {
                                Ln.e(e.getMessage());
                            }
                            break;
                        case ProgressEvent.STARTED_EVENT_CODE:
                            Ln.d("Upload Started: " + upload.getDescription());
                            Toast.makeText(getBaseContext(), "Upload starting for: " + upload.getDescription(), Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        }
    }

}
