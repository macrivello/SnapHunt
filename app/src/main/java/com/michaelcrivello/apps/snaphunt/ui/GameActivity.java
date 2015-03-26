package com.michaelcrivello.apps.snaphunt.ui;

import android.os.Bundle;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.squareup.otto.Bus;

/**
 * Created by michael on 3/24/15.
 */
public class GameActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
    }


}
