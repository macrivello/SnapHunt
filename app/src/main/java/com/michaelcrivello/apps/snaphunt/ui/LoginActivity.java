package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.User;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by michael on 3/18/15.
 */
public class LoginActivity extends BaseActivity {
    @InjectView(R.id.login_username) EditText usernameText;
    @InjectView (R.id.login_password) EditText passwordText;
    @InjectView (R.id.login_submit_button) Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setClickListeners();
    }

    @Override
    protected void autoRefresh(boolean b) {
        // Nothing to do for now
    }

    private void setClickListeners() {
        loginButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                loginWithDevAccount();
                return true;
            }
        });
    }

    // TODO: Login with dev credentials
    private void loginWithDevAccount() {
        attemptLogin("dev", "dev");
    }

    //TODO: Validation, POST to API
    public void onLoginSubmit(View v){
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        if (validateInput(username, password)) {
            attemptLogin(username, password);
        }
    }

    private boolean validateInput(String username, String password) {
        // TODO: Implement proper validation.
        if (username.length() > 0 && password.length() > 0){
            return true;
        }
        return false;
    }

    private void attemptLogin(String username, String password){
        // Login User
        snaphuntApi.loginUser(username, password,
                new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        Ln.d("Login successful", user, response);
                        // Save user
                        userManager.setUser(user);
                        startActivity(new Intent(SnaphuntApp.getInstance(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Ln.e(error);
                    }
                });
    }
}
