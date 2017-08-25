package com.kegy.mobilemedia.utils.user;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.kegy.mobilemedia.MediaApp;
import com.kegy.mobilemedia.model.account.LoginInfo;
import com.kegy.mobilemedia.model.account.TypeList;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.Logger;
import com.kegy.mobilemedia.utils.device.DeviceUtils;
import com.kegy.mobilemedia.utils.device.OperationUtils;
import com.kegy.mobilemedia.utils.http.AsyncHttpClient;
import com.kegy.mobilemedia.utils.http.HttpUtils;
import com.kegy.mobilemedia.utils.http.RequestParams;
import com.kegy.mobilemedia.utils.manager.APIManager;
import com.kegy.mobilemedia.utils.manager.MobileDataManager;

/**
 * 用户登录等工具类
 */
public class Tourist {

    private static Tourist sInstance;

    private Context mContext;

    private Tourist(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static Tourist getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Tourist(context);
        }
        return sInstance;
    }

    public void login() {
        Logger.d("begin login");
        String url = APIManager.LOGIN_API_V1;
        RequestParams params = new RequestParams();
        String deviceId = DeviceUtils.getDeviceId(mContext);
        MobileDataManager.setDeviceId(deviceId);
        params.put("deviceno", DeviceUtils.getDeviceId(mContext));
        params.put("devicetype", Config.DEVICE_TYPE);
        params.put("account", "guest_" + DeviceUtils.getDeviceId(mContext));
        params.put("accounttype", "2");
        params.put("accesstoken", "0");
        params.put("pwd", OperationUtils.getMD5("111111"));
        params.put("isforce", "1");
        HttpUtils.callJSONAPI(url, params, new HttpUtils.StringResponseListener() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Logger.d("login result: " + result);
                    LoginInfo loginInfo = MobileDataManager.getGson().fromJson(result, LoginInfo.class);
                    Logger.d("get token: " + loginInfo.getAccess_token());
                    if (loginInfo.getAccess_token() != null) {
                        MobileDataManager.setAccessToken(loginInfo.getAccess_token());
                        MediaApp.setLoginInfo(loginInfo);
                        getTypeData();
                    } else {
                        Logger.d("login failed");
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void getTypeData() {
        Logger.d("begin getTypeData");
        String url = APIManager.GET_TYPE_LIST;
        RequestParams params = new RequestParams();
        params.put("label", "0");
        params.put("accesstoken", MobileDataManager.getAccessToken());
        final String key = AsyncHttpClient.getUrlWithQueryString(url, params);
        HttpUtils.callJSONAPI(key, new HttpUtils.StringResponseListener() {

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Logger.d("get type data: " + result);
                    TypeList typeList = MobileDataManager.getGson().fromJson(result, TypeList.class);
                    if (typeList != null
                            && typeList.getType_list() != null
                            && typeList.getType_list().size() > 0) {//valid result
                        TypeList.TypeChildren typeChildren = typeList.getType_list().get(0);
                        MediaApp.setTypeChildren(typeChildren);
                    }
                }
            }

            @Override
            public void onError(String error) {
                Logger.d("getTypeData error: " + error);
            }
        });
    }

}
