package com.kegy.mobilemedia.utils.common;

import android.text.TextUtils;

import com.kegy.mobilemedia.utils.Config;

/**
 * Created by Administrator on 2017/8/18.
 */
public class DomainUtils {

    public static boolean isNeed = true;

    /** 替换域名 */
    public static String replaceDomain(String sourseUrl) {
        String newUrl = sourseUrl;

        if (isNeed && !TextUtils.isEmpty(sourseUrl))
            newUrl = replaceDomainAndPort(Config.SERVER_IP, sourseUrl);
        return newUrl;
    }

    public static String replaceDomainAndPort(String domain, String url) {
        String reg = ".*\\/\\/([^\\/\\:]*).*";
        return url.replace(url.replaceAll(reg, "$1"), domain);
    }
}