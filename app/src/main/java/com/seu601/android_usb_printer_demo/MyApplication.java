package com.seu601.android_usb_printer_demo;

import android.app.Application;
import android.content.Context;

/**
 * Created by ylx on 2017/4/21.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
