package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.event.GcmMessage;
import com.michaelcrivello.apps.snaphunt.event.RoundPhotoUpload;
import com.michaelcrivello.apps.snaphunt.event.S3UploadUpload;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by michael on 3/11/15.
 * Sample Activity to test uploading photos to S3.
 */
public class SampleActivity extends RoboActivity {
   // Injected Views
    @InjectView(R.id.selectPhotoText) TextView selectedPhotoText;
    @InjectView(R.id.uploadUrlText) TextView uploadUrlText;
    @InjectView(R.id.progress_wheel) ProgressWheel progressWheel;
    @InjectView(R.id.photoPreview) ImageView photoPreview;

    // Otto Event Bus
    @Inject Bus bus;
    // Retrofit RestAdapter
//    @Inject SnaphuntApi snaphuntApi;

    // File Uploading
    private File selectedPhotoFile;
    private String selectedPhotoFilePath;
    private static final int IMAGE_SELECTED_CODE = 69;
    private static final int REQUEST_IMAGE_CAPTURE = 70;

    // GCM
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_photo_upload_activity);

        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Ln.d("No valid Google Play Services APK found.");
        }
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

        //TESTING RETROFIT
//        List<User> users = snaphuntApi.listUsers();
//        Ln.d(users);
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

    @Subscribe
    public void onGcmMessage(GcmMessage gcmMessage) {
        Toast.makeText(this, "GCM MESSAGE: " + gcmMessage.getMessage(), Toast.LENGTH_LONG).show();
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


    // GCM Related Methods
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Ln.e("Device is not supported");
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Ln.d("Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROPERTY_REG_ID, regId);
        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Ln.d("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Ln.d("App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Constants.GOOGLE_APP_PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Ln.d("GCM Device Registered. RegID: " + regid);
                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(Constants.GCM_PREFS,
                Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }
}
