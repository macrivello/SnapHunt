/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.michaelcrivello.apps.snaphunt.util;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

import roboguice.util.Ln;

/* 
 * This class just handles getting the client since we don't need to have more than
 * one per application
 */
public class S3Util {
    private static AmazonS3Client sS3Client;
    private static CognitoCredentialsProvider sCredProvider;

    public static CognitoCredentialsProvider getCredProvider(Context context) {
        if(sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context,
                    Constants.COGNITO_POOL_ID,    /* Identity Pool ID */
                    Constants.AWS_REGION           /* Region */
            );

            sCredProvider.refresh();
        }
        return sCredProvider;
    }

    public static String getPrefix(Context context) {
        return Constants.PHOTO_UPLOAD_FOLDER + "/";
    }

    public static AmazonS3Client getS3Client(Context context) {
        if(sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context));
        }
        return sS3Client;
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1); 
    }

    public static String getS3ObjectNameHash(String bucket, String key){
        return Integer.toString(new String(bucket + "/" + key).hashCode());
    }


}
