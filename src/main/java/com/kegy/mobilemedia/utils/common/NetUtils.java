package com.kegy.mobilemedia.utils.common;

import android.content.Context;
import android.net.TrafficStats;

/**
 * Created by Administrator on 2017/8/24.
 */

public class NetUtils {
    private static long lastTotalRxBytes = 0;
    private static long lastTimeStamp = 0;

    public static boolean isNetResource(String path) {
        boolean result = false;
        if (path != null) {
            if (path.toLowerCase().startsWith("http") ||
                    path.toLowerCase().startsWith("rtsp") ||
                    path.toLowerCase().startsWith("mms")) {
                result = true;
            }
        }
        return result;
    }

    public static String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed = String.valueOf(speed) + " kb/s";
        return netSpeed;
    }
}
