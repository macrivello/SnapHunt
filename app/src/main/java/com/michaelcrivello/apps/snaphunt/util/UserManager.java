package com.michaelcrivello.apps.snaphunt.util;

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
@Singleton
public class UserManager {
    @Inject ApiHeaders apiHeaders;
    @Inject SnaphuntApi snaphuntApi;
    @Inject Bus bus;

    User user;

    public void setUser(User user) {
        Ln.d("Setting new logged in User");
        this.user = user;
        apiHeaders.setAuthToken(user.getAuthToken());

        bus.post(new UserLogin(user));
        checkGcmRegId();
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
    }
}
