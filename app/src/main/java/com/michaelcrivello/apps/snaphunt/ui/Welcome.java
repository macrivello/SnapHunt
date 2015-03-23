package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.service.S3TransferService;

import roboguice.activity.RoboActivity;
import roboguice.util.Ln;

/**
 * Created by michael on 3/18/15.
 */
public class Welcome extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    }

    public void onLogin(View v) {
        startActivity(new Intent(this, Login.class));
        overridePendingTransition(0, 0);
    }
    public void onRegister(View v){
        startActivity(new Intent(this, Register.class));
        overridePendingTransition(0, 0);
    }
}
