package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.event.GcmRegistered;
import com.michaelcrivello.apps.snaphunt.event.GcmUnregistered;
import com.michaelcrivello.apps.snaphunt.event.PhotoReadyForSubmit;
import com.michaelcrivello.apps.snaphunt.event.S3TransferManagerUpdated;
import com.michaelcrivello.apps.snaphunt.event.S3Upload;
import com.michaelcrivello.apps.snaphunt.util.GcmUtil;
import com.michaelcrivello.apps.snaphunt.util.UserManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboActionBarActivity;
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
    TransferManager transferManager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getBaseContext();
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
        @Subscribe
        public void updateTransferManager (S3TransferManagerUpdated transferManagerUpdated) {
            Ln.d("updateTransferManager");
            transferManager = transferManagerUpdated.getTransferManager();
        }
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
//    public void onS3Upload(S3Upload s3UploadUpload){
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
