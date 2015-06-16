package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.os.Bundle;

import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.google.android.gms.common.api.Api;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.event.GcmRegistered;
import com.michaelcrivello.apps.snaphunt.event.GcmUnregistered;
import com.michaelcrivello.apps.snaphunt.event.PhotoReadyForSubmit;
import com.michaelcrivello.apps.snaphunt.event.S3UploadUpload;
import com.michaelcrivello.apps.snaphunt.util.GcmUtil;
import com.michaelcrivello.apps.snaphunt.util.UserManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;

import roboguice.activity.RoboActionBarActivity;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboFragmentActivity;
import roboguice.fragment.RoboFragment;
import roboguice.util.Ln;

/**
 * Created by michael on 3/19/15.
 */
public class BaseActivity extends RoboActionBarActivity {
    @Inject SnaphuntApi snaphuntApi;
    @Inject ApiHeaders apiHeaders;
    @Inject Bus bus;
    @Inject UserManager userManager;
    BaseActivityBusListener baseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseListener = new BaseActivityBusListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(baseListener);

        if (GcmUtil.getRegistrationId().isEmpty()) {
            GcmUtil.register();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(baseListener);
    }

    protected class BaseActivityBusListener {
        @Subscribe
        public void onGcmRegistered (GcmRegistered gcmRegistered) {
            Ln.d("onGcmRegistered");
            userManager.updateUserGcmId(gcmRegistered.getRegId());
        }
        @Subscribe
        public void onGcmUnregistered (GcmUnregistered gcmUnregistered) {
            Ln.d("onGcmUnregistered");
        }
    }

////    protected class GameEventListener {
//        // Event Subscription
//    @Subscribe
//    public void onPhotoReady(PhotoReadyForSubmit photoReadyForSubmit){
//        Ln.d("onPhotoReady");
//
//        handlePhotoReady(photoReadyForSubmit);
//    }
//    @Subscribe
//    public void onS3Upload(S3UploadUpload s3UploadUpload){
//        Ln.d("onS3Upload");
//
//        handleS3Upload(s3UploadUpload);
//    }
////    }

    protected void handleS3Upload(S3UploadUpload s3UploadUpload) {
        // This method is meant to be Overriden by child activity
        Ln.d("handleS3Upload");

    }

    protected void handlePhotoReady(PhotoReadyForSubmit photoReadyForSubmit) {
        // This method is meant to be Overriden by child activity
        Ln.d("handlePhotoReady");
    }

    protected void logout() {
        userManager.clearUser();
        startActivity(new Intent(this, WelcomeActivity.class));
        overridePendingTransition(0, 0);
    }

//    @Subscribe
//    public void onPhotoReady(PhotoReadyForSubmit photoReadyForSubmit){
//        Ln.d("onPhotoReady");
//
//        handlePhotoReady(photoReadyForSubmit);
//    }
//    @Subscribe
//    public void onS3Upload(S3UploadUpload s3UploadUpload){
//        Ln.d("onS3Upload");
//
//        handleS3Upload(s3UploadUpload);
//    }
//    @Subscribe
//    public void onGcmRegistered (GcmRegistered gcmRegistered) {
//        Ln.d("onGcmRegistered");
//        userManager.updateUserGcmId(gcmRegistered.getRegId());
//    }
//    @Subscribe
//    public void onGcmUnregistered (GcmUnregistered gcmUnregistered) {
//        Ln.d("onGcmUnregistered");
//    }
}
