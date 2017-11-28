package com.vikas.dreamworthtest;

import android.app.Application;

import com.cloudinary.android.LogLevel;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.policy.GlobalUploadPolicy;
import com.cloudinary.android.policy.UploadPolicy;
import com.facebook.stetho.Stetho;

/**
 * Created by Vikas on 11/23/2017.
 */

public class MainApplication extends Application {
    static MainApplication _instance;

    public static MainApplication get() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        // This can be called any time regardless of initialization.
        MediaManager.setLogLevel(LogLevel.DEBUG);

        // Mandatory - call a flavor of init. Config can be null if cloudinary_url is provided in the manifest.
        MediaManager.init(this);
        // Optional - configure global policy.
        MediaManager.get().setGlobalUploadPolicy(
                new GlobalUploadPolicy.Builder()
                        .maxConcurrentRequests(4)
                        .networkPolicy(UploadPolicy.NetworkType.ANY)
                        .build());

        _instance = this;
    }
}