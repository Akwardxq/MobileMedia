package com.kegy.mobilemedia.utils;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Administrator on 2017/8/24.
 */

public final class Config {

    public static String DEVICE_TYPE = "3";


    @Deprecated
    public static int sDeviceWidth;

    @Deprecated
    public static int sDeviceHeight;

    public static String TOP_POSTER_SIZE = "640x338";   //部分地区大海报尺寸不是这一个，比如宁夏
    public static final String NORMAL_POSTER_SIZE = "246x138";
    public static String VERTICAL_POSTER_SIZE = "160x200";

    public static final String PAD_TOP_BIG_POSTER_SIZE = "500x280";
    public static final String PAD_BIG_POSTER_SIZE = "500x280";
    public static final String PAD_NORMAL_POSTER_SIZE = "500x280";
    public static final String PAD_VERTICAL_POSTER_SIZE = "320x400";
    public static String CHANNEL_POSTER_SIZE = "90x90";

    public static int LABEL_CHANNEL = 1; // 频道
    public static int LABEL_VOD = 2; // 点播
    public static int LABEL_SERIES = 3; // 电视剧
    public static int LABEL_ZONGYI = 4; // 综艺
    public static int LABEL_JIAOYU = 5; // 教育
    public static int LABEL_LIFE = 6; // 生活
    public static int LABEL_MUSIC = 7; // 音乐
    public static int LABEL_NEWS = 8; // 资讯
    public static int LABEL_MONITOR = 9; // 资讯
    public static int LABEL_LOOKBACK = 10; // 回看
    public static int LABEL_SPORT = 11; // 体育
    public static int LABEL_JISHI = 12; // 纪实
    public static int LABEL_APPLICATION = 13; // 应用
    public static int LABEL_DONGMAN = 14; // 动漫

    public static int LABEL_IPANEL = 1001; // 茁壮网络
    public static int LABEL_DIANSHANG=1100;//电商
    public static int LABEL_JIAYULVYOU=1314;//家峪旅游
    public static int LABEL_BO_SHOW=16;//直播秀

    public static String SERVER_IP="";

    public static final String SERVER_SLAVE = "http://slave.homed.me:12690/";

    public static final String SERVER_SLAVE1 = "http://slave.homed.me:13160/";

    public static DisplayMetrics dm = null;

    public static float dp2px(float dpSize) {
        float pxSize = 0;
        if (dm != null)
            pxSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpSize, dm);
        return pxSize;
    }

    public static float sp2px(float spSize) {
        float pxSize = 0;
        if (dm != null)
            pxSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    spSize, dm);
        return pxSize;
    }

}
