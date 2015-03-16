package com.michaelcrivello.apps.snaphunt.util;


import com.amazonaws.regions.Regions;

/**
 * Created by michael on 3/11/15.
 */
public class Constants {
    public static final String AWS_ACCOUNT_ID = "122085378912";
    public static final String COGNITO_POOL_ID =
            "us-east-1:4011341e-e994-4af4-97f5-7bf5f03e04bc";
    public static final String COGNITO_ROLE_UNAUTH =
            "arn:aws:iam::122085378912:role/snaphunt_client";
    public static final String COGNITO_ROLE_AUTH =
            "arn:aws:iam::122085378912:role/snaphunt_client";
    public static final String BUCKET_NAME = "snaphunt-storage";
    public static final Regions AWS_REGION = Regions.US_EAST_1;


    public static final String PHOTO_UPLOAD_FOLDER = "photo-upload";
}
