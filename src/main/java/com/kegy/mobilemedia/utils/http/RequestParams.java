package com.kegy.mobilemedia.utils.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/8/17.
 */

public class RequestParams {
    private static final String ENCODING = "UTF-8";

    private ConcurrentHashMap<String, String> mUrlParams;

    public RequestParams() {
        mUrlParams = new ConcurrentHashMap<>();
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            mUrlParams.put(key, value);
        }
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<>();

        for (ConcurrentHashMap.Entry<String, String> entry : mUrlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return lparams;
    }

    /**
     * Returns an HttpEntity containing all request parameters
     */
    public HttpEntity getEntity() {
        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public String getParamString() {
        return URLEncodedUtils.format(getParamsList(), ENCODING);
    }

}
