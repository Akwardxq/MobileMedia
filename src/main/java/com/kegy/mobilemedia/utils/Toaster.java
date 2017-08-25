package com.kegy.mobilemedia.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by kegy on 2017/8/8.
 */

public final class Toaster {

    public static void toast(Context context,String msg) {
        toast(context,msg,false);
    }

    public static void toast(Context context,String msg,boolean isLong) {
        Toast.makeText(context,msg,isLong?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
    }

}
