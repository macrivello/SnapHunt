package com.michaelcrivello.apps.snaphunt.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.util.GCMUtil;

import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * Created by michael on 3/18/15.
 */
public class RegisterActivity extends BaseActivity {
    @InjectView (R.id.register_email) EditText emailText;
    @InjectView (R.id.register_username) EditText usernameText;
    @InjectView (R.id.register_password) EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        initInput();
        initInputWatchers();
    }

    @Override
    protected void autoRefresh(boolean b) {
        // Nothing to do for now
    }

    // TODO: Implement text watchers to provide realtime feedback
    // TODO: look into RxSamples for this^
    private void initInputWatchers() {
        emailText.requestFocus();
    }

    // TODO: Check shared prefs for saved input
    private void initInput() {
        // Fill in email address for convenience
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        // Grab the first email address in Accounts and set as email field
        for (Account account : accounts){
            if (emailPattern.matcher(account.name).matches()) {
                emailText.setText(account.name);
                break;
            }
        }
    }

    //TODO: Validation, POST to API
    public void onRegisterSubmit(View v) {
        // Register User, check for GCM Id
        String username = usernameText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String gcmRegId;

        // TODO: Refactor
//        if (!InputValidator.isValid(username, InputValidator.INPUT_TYPE.USERNAME)) {
//            Toast.makeText(this, R.string.valid_username, Toast.LENGTH_LONG).show();
//            return;
//        } else if (!InputValidator.isValid(email, InputValidator.INPUT_TYPE.EMAIL)) {
//            Toast.makeText(this, R.string.valid_email, Toast.LENGTH_LONG).show();
//            return;
//        } else if (!InputValidator.isValid(password, InputValidator.INPUT_TYPE.PASSWORD)) {
//            Toast.makeText(this, R.string.valid_password, Toast.LENGTH_LONG).show();
//            return;
//        }

        // TODO: I need to ensure as best as possible that they can have GCM enabled and a reg id
        gcmRegId = GCMUtil.getRegistrationId();
        if (gcmRegId.isEmpty()) {
            Ln.e("Registering user with no GCM Id");
        }

        User tempUser = new User();
        tempUser.setEmail(email);
        tempUser.setUsername(username);
        tempUser.setPassword(password);
        tempUser.setGcmRegId(gcmRegId);

        registerUser(tempUser);
    }

    /**
     *    The user should only be a temporary object used for format consistency.
     *    The returned User should be stored.
     */
    private void registerUser(User user) {
        snaphuntApi.registerUser(user,
                new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        // TODO: Store User, update Request interceptor
                        userManager.setUser(user);

//                        Toast.makeText(Register.this, "Successfully Registered. Welcome.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SnaphuntApp.getInstance(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Ln.e("Error Registering.", error);
                        Toast.makeText(RegisterActivity.this, "Error Registering.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
