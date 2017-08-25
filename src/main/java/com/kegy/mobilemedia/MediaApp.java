package com.kegy.mobilemedia;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by Administrator on 2017/8/24.
 * key:599fbaa3
 */

public class MediaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化科大讯飞语音输入
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=599fbaa3");
    }

}
