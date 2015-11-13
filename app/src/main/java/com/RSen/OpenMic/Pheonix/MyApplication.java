package com.RSen.OpenMic.Pheonix;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by rsen on 11/13/15.
 */
public class MyApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
