package com.kegy.mobilemedia.controller.base;

import android.app.Activity;

/**
 * Created by Administrator on 2017/8/24.
 */

public abstract class BaseVideoPlayerActivity extends Activity {

    public abstract void playBy(int direction);
    public abstract void playTo(int temp);
}