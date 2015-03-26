package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.michaelcrivello.apps.snaphunt.R;

/**
 * Created by michael on 3/18/15.
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    }

    public void onLogin(View v) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(0, 0);
    }
    public void onRegister(View v){
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(0, 0);
    }
}
