package com.michaelcrivello.apps.snaphunt.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.michaelcrivello.apps.snaphunt.data.api.ApiHeaders;
import com.michaelcrivello.apps.snaphunt.data.api.SnaphuntApi;
import com.michaelcrivello.apps.snaphunt.data.model.user.User;
import com.michaelcrivello.apps.snaphunt.event.UserUpdate;
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

    public User getUser() {
        return user;
    }

    //TODO: Save in SharedPrefs
    public void setUser(User user) {
        Ln.d("Setting new logged in User");
        this.user = user;
        apiHeaders.setAuthToken(user.getAuthToken());

        storeUser(user);

        bus.post(new UserUpdate(user));
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

    // TODO: Update to use InstanceID instead of GcmUtil.register()
    private void checkGcmRegId() {
        String gcmRegId = GCMUtil.getRegistrationId();

        // If there is a valid gcmId stored, update User.
        if (!gcmRegId.isEmpty()) {
            if (user.getGcmRegId() == null || !user.getGcmRegId().equals(gcmRegId)) {
                updateUserGcmId(gcmRegId);
            }
        } else {
            GCMUtil.register();
        }
    }


    /*    Update user on server with new GCM Token.
    *
    *   Send serialized User object with only GCM Token set. Server
    *   will only update this field in DB.
    */
    public void updateUserGcmId(final String gcmRegId){
        if (user == null){
            return;
        }

        if (user != null && user.getGcmRegId().equals(gcmRegId)){
            Ln.d("GCM Reg ID is identical, not updating user in DB");
            return;
        }

        User tmpUser = new User();
        tmpUser.setGcmRegId(gcmRegId);

        try{
            snaphuntApi.updateUser(tmpUser, user.getId().toHexString(), new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    Ln.d("User's GcmRegId updated");

                    // TODO: If I receive a new user object, should I update the user stored in user
                    //manager?
                    user.setGcmRegId(gcmRegId);
                    bus.post(new UserUpdate(user));
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

    public String getUserId() {
        return user != null ? user.getId().toHexString() : "";
    }

}
