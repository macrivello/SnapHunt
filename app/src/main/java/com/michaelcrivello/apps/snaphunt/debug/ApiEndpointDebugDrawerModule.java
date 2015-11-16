package com.michaelcrivello.apps.snaphunt.debug;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.SharedPrefsUtil;

import java.util.HashMap;

import io.palaima.debugdrawer.module.DrawerModule;
import io.palaima.debugdrawer.module.NetworkModule;

/**
 * Created by tao on 11/12/15.
 */
public class ApiEndpointDebugDrawerModule implements DrawerModule {
    private final Context context;
    private RadioGroup radioGroup;
    private RadioButton local, localEmu, remote;

    private static SharedPreferences sharedPreferences = SharedPrefsUtil.sharedPreferences;

    public ApiEndpointDebugDrawerModule(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        View v = inflater.inflate(R.layout.debug_drawer_item_api, parent, false);

        radioGroup = (RadioGroup) v.findViewById(R.id.debug_api_endpoint_group);

        local = (RadioButton) v.findViewById(R.id.local_endpoint_rbutton);
        local.setText(Constants.LOCAL_SERVER_WAN);

        localEmu = (RadioButton) v.findViewById(R.id.local_emulator_endpoint_rbutton);
        localEmu.setText(Constants.LOCAL_SERVER_LAN_EMULATOR);

        remote = (RadioButton) v.findViewById(R.id.remote_endpoint_rbutton);
        remote.setText(Constants.REMOTE_SERVER);

        String endpoint = sharedPreferences.getString(Constants.API_ENDPOINT_KEY, SnaphuntApi.API_ENDPOINT);

        switch (endpoint) {
            case SnaphuntApi.API_ENDPOINT:
                remote.setChecked(true);
                break;
            case SnaphuntApi.API_ENDPOINT_LOCAL:
                local.setChecked(true);
                break;
            case SnaphuntApi.API_ENDPOINT_EMULATOR_LOCAL:
                localEmu.setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener(((buttonView, id) -> {
            String url = null;

            switch (id) {
                case R.id.local_endpoint_rbutton:
                    url = SnaphuntApi.API_ENDPOINT_LOCAL;
                    break;
                case R.id.local_emulator_endpoint_rbutton:
                    url = SnaphuntApi.API_ENDPOINT_EMULATOR_LOCAL;
                    break;
                case R.id.remote_endpoint_rbutton:
                    url = SnaphuntApi.API_ENDPOINT;
                    break;
            }

            updateEndpoint(url);
        }));

        return v;
    }

    private void updateEndpoint(String url) {
        if (url != null && sharedPreferences.edit().putString(Constants.API_ENDPOINT_KEY, url).commit()){
            ProcessPhoenix.triggerRebirth(context);
        }
    }

    @Override
    public void onOpened() {

    }

    @Override
    public void onClosed() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
