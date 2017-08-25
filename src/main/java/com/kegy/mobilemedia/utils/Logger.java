package com.kegy.mobilemedia.utils;

import android.util.Log;

/**
 * Created by kegy on 2017/8/8.
 */

public final class Logger {
    private static final String TAG = "MobileMedia";
    private static final boolean DEBUG = true;

    public static void d(String msg) {
        if (DEBUG)
            Log.i(TAG, msg);
    }

    public static void e(String msg, Exception e) {
        if (DEBUG)
            Log.e(TAG, msg, e);
    }

    public static void e(String msg) {
        if (DEBUG)
            Log.e(TAG, msg);
    }

}
