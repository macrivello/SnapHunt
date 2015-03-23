/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michaelcrivello.apps.snaphunt.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.event.GcmMessage;
import com.michaelcrivello.apps.snaphunt.event.GcmRegistered;
import com.michaelcrivello.apps.snaphunt.event.GcmUnregistered;
import com.michaelcrivello.apps.snaphunt.receiver.GcmBroadcastReceiver;
import com.michaelcrivello.apps.snaphunt.ui.SampleActivity;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.GcmUtil;
import com.squareup.otto.Bus;

import java.io.IOException;

import roboguice.service.RoboIntentService;
import roboguice.util.Ln;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends RoboIntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    @Inject Bus bus;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (messageType == null) {
                // Non GCM Intent
                int task = extras.getInt(Constants.GCM_INTENT_TASK, -1);
                switch (task) {
                    case Constants.GCM_REGISTER:
                        registerInBackground();
                        break;
                    case Constants.GCM_UNREGISTER:
                        unregister();
                        break;
                }
            }
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Ln.d("Received GCM message.", extras.toString());

                if (bus != null) {
                    bus.post(new GcmMessage(extras.toString()));
                }

                // TODO: Add logic on how and when to display notifications in Notification center
                // Post notification of received message.
//                sendNotification("Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SampleActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("GCM Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    public void registerInBackground() {
        if (GcmUtil.checkPlayServices()) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    String regid = "";
                    try {
                        regid = GoogleCloudMessaging.getInstance(SnaphuntApp.getInstance()).register(Constants.GOOGLE_APP_PROJECT_NUMBER);
                        msg = "Device registered, registration ID=" + regid;
                        Ln.d("GCM Device Registered. RegID: " + regid);
                        // You should send the registration ID to your server over HTTP, so it
                        // can use GCM/HTTP or CCS to send messages to your app.

                        // sendRegistrationIdToBackend();

                        // For this demo: we don't need to send it because the device will send
                        // upstream messages to a server that echo back the message using the
                        // 'from' address in the message.

                        // Persist the regID - no need to register again.
                        GcmUtil.storeRegistrationId(regid);
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                        // If there is an error, don't just keep trying to register.
                        // Require the user to click a button again, or perform
                        // exponential back-off.
                    }
                    return regid;
                }

                @Override
                protected void onPostExecute(String regId) {
                    Ln.d("New GCM Reg Id: " + regId);
                    bus.post(new GcmRegistered(regId));
                }
            }.execute(null, null, null);
        } else {
            Ln.e("Cant register for GCM since PlayServices isn't installed");
        }

    }

    public void unregister() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging.getInstance(SnaphuntApp.getInstance()).unregister();
                } catch (IOException ex) {
                    Ln.e("Error unregistering GCM.", ex);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Ln.d("Unregistered GCM");
                bus.post(new GcmUnregistered());
            }
        }.execute(null, null, null);
    }

}
