package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.SharedPrefsUtil;
import com.michaelcrivello.apps.snaphunt.util.UserManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by michael on 3/18/15.
 */
public class WelcomeActivity extends BaseActivity {
    @InjectView(R.id.fb_login_button)
    LoginButton fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userId = SharedPrefsUtil.sharedPreferences.getString(Constants.USER_ID_KEY, null);
        String userToken = SharedPrefsUtil.sharedPreferences.getString(Constants.USER_TOKEN_KEY, null);;

        // Check if there is a user stored. If so, log in.
        if (userId != null && userToken != null) {
            // Set authtoken in request headers
            apiHeaders.setAuthToken(userToken);

            snaphuntApi.getUser(userId, new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    // TODO: Update user info and stuff...
                    userManager.setUser(user);

                    loadHomeScreen();
                }

                @Override
                public void failure(RetrofitError error) {
                    //
                }
            });
        }

        // Quick Fade in welcome screen
        setContentView(R.layout.welcome);

        fbLoginButton.setReadPermissions("user_friends");
        fbLoginButton.registerCallback(CallbackManager.Factory.create(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Ln.d("FB login success.");

                loginResult.getAccessToken();
            }

            @Override
            public void onCancel() {
                Ln.d("FB login cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Ln.d("FB login error");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void autoRefresh(boolean b) {
        // Nothing to do for now
    }

    // Loads Home Screen
    // TODO: change activity
    private void loadHomeScreen() {
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK));
        overridePendingTransition(0, 0);
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
