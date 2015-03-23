package com.michaelcrivello.apps.snaphunt.event;

import com.michaelcrivello.apps.snaphunt.data.model.User;

/**
 * Created by michael on 3/19/15.
 */
public class UserLogin {
    User user;

    public UserLogin(User u){
        user = u;
    }

    public User getUser() {
        return user;
    }
}
