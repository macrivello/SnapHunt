package com.michaelcrivello.apps.snaphunt.data.api;

import android.webkit.MimeTypeMap;

import com.amazonaws.org.apache.http.entity.ContentType;
import com.google.inject.Singleton;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.squareup.okhttp.MediaType;


import org.apache.http.protocol.HTTP;

import retrofit.RequestInterceptor;
import retrofit.mime.MimeUtil;

/**
 * This class helps add headers to API requests.
 */
@Singleton
public class ApiHeaders implements RequestInterceptor {
    String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void clearAuthValue(String authValue) {
        this.authToken = null;
    }

    @Override
    public void intercept(RequestFacade request) {
        String authToken = this.authToken;
        
        if (authToken != null) {
            request.addHeader(Constants.AUTH_HEADER, authToken);
        }
        request.addHeader(HTTP.CONTENT_TYPE, Constants.HTTP_CONTENT_TYPE_JSON);
    }
}
