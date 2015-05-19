package com.michaelcrivello.apps.snaphunt.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.User;
import com.michaelcrivello.apps.snaphunt.event.UserLogin;
import com.squareup.otto.Bus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.util.Ln;

/**
    This class will contain information about current user.
    It should be a singleton and be injectable. This will allow easier use of
    binding to mock user for testing.
 */

// TODO: Subscribe to Login, Logout events
@Singleton
public class UserManager {
    @Inject ApiHeaders apiHeaders;
    @Inject SnaphuntApi snaphuntApi;
    @Inject Bus bus;

    User user;

    //TODO: Save in SharedPrefs
    public void setUser(User user) {
        Ln.d("Setting new logged in User");
        this.user = user;
        apiHeaders.setAuthToken(user.getAuthToken());

        storeUser(user);

        bus.post(new UserLogin(user));
        checkGcmRegId();
    }

    //Store User in SharedPreferences
    private void storeUser(User user) {
        if (user != null) {
            Ln.d("Storing user in sharedPrefs", user.getId(), user.getAuthToken());
            SharedPrefsUtil.sharedPreferences.edit()
                    .putString(Constants.USER_ID_KEY, user.getId().toHexString())
                    .putString(Constants.USER_TOKEN_KEY, user.getAuthToken())
                    .apply();
        }
    }

    private void checkGcmRegId() {
        final String gcmRegId = GcmUtil.getRegistrationId();

        // If there is a valid gcmId stored, update User.
        if (!gcmRegId.isEmpty()) {
            if (user.getGcmRegId() == null || !user.getGcmRegId().equals(gcmRegId)) {
                updateUserGcmId(gcmRegId);
            }
        } else {
            GcmUtil.register();
        }
    }

    public User getUser() {
        return user;
    }

    public void updateUserGcmId(final String gcmRegId){
        User tmpUser = new User();
        tmpUser.setGcmRegId(gcmRegId);

        try{
            snaphuntApi.updateUser(tmpUser, user.getId().toHexString(), new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    Ln.d("User's GcmRegId updated");
                    user.setGcmRegId(gcmRegId);
                }

                @Override
                public void failure(RetrofitError error) {
                    Ln.e(error);
                }
            });
        }catch (Exception e){
            Ln.e("Error updating user.", e);
        }

    }

    // TODO: Make sure any pending changes are saved
    public void clearUser() {
        user = null;

        SharedPrefsUtil.sharedPreferences.edit().clear().apply();
    }
}
