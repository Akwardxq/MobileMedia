package com.kegy.mobilemedia.utils.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/24.
 */


public  class TimeUtils {

    private static final SimpleDateFormat DETAILS = new SimpleDateFormat("HH:mm:ss");

    public static String currentTimeStr() {
        return DETAILS.format(new Date());
    }

    public static String toTimeStr(long timeMs) {
        int secTotal = (int) (timeMs / 1000);
        String result = null;
        int min = secTotal / 60;
        int sec = secTotal % 60;
        result = to2Str(min) + ":" + to2Str(sec);
        return result;
    }

    private static String to2Str(int i) {
        if (i > 9) {
            return i + "";
        } else {
            return "0" + i;
        }
    }

    public static long getUTCtime()
    {
        //输入：无
        //输出：当前UTC时间
        long time = System.currentTimeMillis();
        return time/1000;
    }

}