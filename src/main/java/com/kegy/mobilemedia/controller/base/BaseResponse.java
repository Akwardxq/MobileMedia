package com.kegy.mobilemedia.controller.base;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/17.
 */

public class BaseResponse implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4474244910778049558L;

    //	@Expose
//	private long ret;
//	@Expose
//	private String ret_msg;
//	@Expose
//	public String message;
    public int ret;
    public String ret_msg;
    public String message;

    public boolean isSuccess() {
        return ret == 0;
    }

    public String getErrorMessage() {
        if (TextUtils.isEmpty(message)) {
            return ret + " " + ret_msg;
        }
        return message;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
