package com.kegy.mobilemedia.utils.http;


import com.kegy.mobilemedia.utils.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/17.
 */

public class HttpUtils {

    private static ExecutorService sPool = Executors.newCachedThreadPool();

    public static void callJSONAPI(final String url, final StringResponseListener listener) {
        Logger.d("call JSON API url: " + url);
        sPool.submit(new Callable<String>() {
            @Override
            public String call() {
                try {
                    String result = getStringFromUrl(url);
                    if (listener != null) {
                        listener.onSuccess(result);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e.getMessage());
                    }
                }
                return null;
            }
        });
    }

    private static String getStringFromUrl(String path) throws Exception {
        String result = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(8000);
            int status = connection.getResponseCode();
            Logger.d("connection status " + status);
            InputStream is;
            if (status == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
            StringBuffer sb = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                sb.append(readLine);
            }
            result = sb.toString();
            Logger.d(result);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public static interface StringResponseListener {
        void onSuccess(String result);

        void onError(String error);
    }

}
