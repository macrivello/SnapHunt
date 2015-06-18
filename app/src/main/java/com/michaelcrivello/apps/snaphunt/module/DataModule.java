package com.michaelcrivello.apps.snaphunt.module;

import android.app.Application;
import android.net.Uri;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import roboguice.util.Ln;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by tao on 6/18/15.
 */
public class DataModule extends AbstractModule {
    @Inject SnaphuntApp app;
    static final int DISK_CACHE_SIZE = 50 * 1024;

    @Override
    protected void configure() {

    }

    @Provides @Singleton Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Ln.e(e, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = null;
        try {
            cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
            Ln.e(e, "Error building httpclient cache.");
        }
        client.setCache(cache);

        return client;
    }
}
