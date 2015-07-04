package com.michaelcrivello.apps.snaphunt.util;


import com.amazonaws.regions.Regions;

/**
 * Created by michael on 3/11/15.
 */
public class Constants {
    // AWS
    public static final String AWS_ACCOUNT_ID = "122085378912";
    public static final String COGNITO_POOL_ID =
            "us-east-1:4011341e-e994-4af4-97f5-7bf5f03e04bc";
    public static final String BUCKET_NAME = "snaphunt-storage";
    public static final Regions AWS_REGION = Regions.US_EAST_1;
    public static final String PHOTO_UPLOAD_FOLDER = "photo-upload";

    // GCM
    public static final String GCM_PREFS = "gcm_prefs";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String GOOGLE_APP_PROJECT_NUMBER = "836468531122";
    public static final String GCM_INTENT_TASK = "gcm_intent_task";
    public static final int GCM_REGISTER = 1;
    public static final int GCM_UNREGISTER = 2;

    // API
    public static final String AUTH_HEADER = "x-auth-token";
    public static final String GCM_HEADER = "gcm-reg-id";
    public static final String HTTP_CONTENT_TYPE_JSON = "application/json";

    // SharedPrefs
    public static final String SHARED_PREFS = "SNAPHUNT_DATA";
    public static final String USER_ID_KEY = "SNAPHUNT_USER_ID_KEY";
    public static final String USER_TOKEN_KEY = "SNAPHUNT_USER_TOKEN_KEY";

    // Intent Bundle Keys
    public static final String GAME_ID_KEY = "GAME_ID_KEY";
    public static final String GAME_KEY = "GAME_KEY";
    public static final String ACCEPTING_INVITE = "ACCEPTING_INVITE";


    // Game Related
    public static final int NUMBER_OR_ROUNDS_DEFAULT = 10;
    public static final int ROUND_TIME_LIMIT = 720; // 720 minutes == 12 hours
    public static final int MAX_USERS_PER_GAME = 10; // For Selectable adapter


}
