package com.michaelcrivello.apps.snaphunt.service;

import android.content.Intent;
import android.os.IBinder;

import com.google.inject.Inject;
import com.squareup.otto.Bus;

import roboguice.service.RoboService;

/**
 * Created by michael on 3/11/15.
 */
public class S3TransferService extends RoboService {

    @Inject
    Bus bus;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
