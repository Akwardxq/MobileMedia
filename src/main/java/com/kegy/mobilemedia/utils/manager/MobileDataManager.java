package com.kegy.mobilemedia.utils.manager;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/8/17.
 */

public class MobileDataManager {

    private static final Gson GSON = new Gson();
    private static String sAccessToken;
    private static String sDeviceId;

    public static String getDeviceId() {
        return sDeviceId;
    }

    public static void setDeviceId(String deviceId) {
        sDeviceId = deviceId;
    }

    public static void setAccessToken(String token) {
        sAccessToken = token;
    }

    public static String getAccessToken() {
        return sAccessToken;
    }

    public static Gson getGson() {
        return GSON;
    }


}
