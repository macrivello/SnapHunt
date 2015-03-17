package com.michaelcrivello.apps.snaphunt.model;

import java.util.List;

/**
 * Created by miccrive on 3/16/15.
 */
public class User {
    String email;
    String username;
    String type;
    String authToken;
    String gcmRegId;
    String profilePhoto;
    String password;
    String provider;
    String providerId;
    Object providerData;
    List<String> games;
    List<String> invitations;
}

