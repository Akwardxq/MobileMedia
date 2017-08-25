package com.kegy.mobilemedia.utils.common;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/8/24.
 */

public class SizeUtils {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public static String toSizeStr(long size) {
        double result = (double)size / (double)(1024 * 1024);
        return FORMAT.format(result) + "m";
    }

}