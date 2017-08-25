package com.kegy.mobilemedia.utils.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DeviceUtils {
    public static String getDeviceId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("UniqueDeviceId", 0);
        String id = sp.getString("UniqueDeviceId", null);
        if (id != null) {
            System.err.println("-------------deviceid："+id);
            return id;
        }

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String DeviceId, androidId, m_szWLANMAC;
        try{
            DeviceId = tm != null ? tm.getDeviceId() : null;
        }catch(Exception e){//android 6.0没有权限的时候会发生异常
            DeviceId=null;
        }

        androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        m_szWLANMAC = (wm != null && wm.getConnectionInfo() != null) ? wm.getConnectionInfo()
                .getMacAddress() : null;

        String m_szLongID = DeviceId + androidId + m_szWLANMAC;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            // get md5 bytes
            byte p_md5Data[] = m.digest();
            // create a hex string
            String m_szUniqueID = new String();
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);
                // if it is a single digit, make sure it have 0 in front (proper
                // padding)
                if (b <= 0xF)
                    m_szUniqueID += "0";
                // add number to string
                m_szUniqueID += Integer.toHexString(b);
            }
            // hex string to uppercase
            m_szUniqueID = m_szUniqueID.toUpperCase();
            System.err.println("--------------deviceid2:"+m_szUniqueID);
            sp.edit().putString("UniqueDeviceId", m_szUniqueID).commit();
            return m_szUniqueID;
        } catch (NoSuchAlgorithmException e) {
            final String uuid = sp.getString("UniqueDeviceId", UUID.randomUUID().toString());
            sp.edit().putString("UniqueDeviceId", uuid).commit();
            return uuid;
        }

    }

}
