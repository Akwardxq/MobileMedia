package com.kegy.mobilemedia.utils.device;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/8/17.
 */

public class OperationUtils {

    public static String getMD5(String str) {              //MD5加密
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buffer = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buffer.append("0");
                buffer.append(Integer.toHexString(i));
            }
            result = buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
