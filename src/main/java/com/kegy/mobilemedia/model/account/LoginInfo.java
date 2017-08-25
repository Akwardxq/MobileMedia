package com.kegy.mobilemedia.model.account;

import com.google.gson.annotations.Expose;
import com.kegy.mobilemedia.controller.base.BaseResponse;

import java.io.Serializable;

/**
 * {
 "access_token" : "R59955D7CU308BE023K773B3902I7816A8C0PBM2FFB583WDAB5A93BBC1",
 "area_code" : "",
 "default_play_mode" : [
 {
 "mode" : 1,
 "platform" : 1,
 "type" : 1
 },
 {
 "mode" : 2,
 "platform" : 1,
 "type" : 2
 },
 {
 "mode" : 2,
 "platform" : 1,
 "type" : 3
 },
 {
 "mode" : 2,
 "platform" : 1,
 "type" : 4
 },
 {
 "mode" : 2,
 "platform" : 1,
 "type" : 5
 },
 {
 "mode" : 1,
 "platform" : 2,
 "type" : 1
 },
 {
 "mode" : 2,
 "platform" : 2,
 "type" : 2
 },
 {
 "mode" : 1,
 "platform" : 2,
 "type" : 3
 },
 {
 "mode" : 1,
 "platform" : 2,
 "type" : 4
 },
 {
 "mode" : 2,
 "platform" : 2,
 "type" : 5
 }
 ],
 "device_id" : 2000369922,
 "extend" : null,
 "home_id" : 312100,
 "icon_url" : {
 "140x140" : "http://apps.homed.me/sys_img/role/user0.jpg"
 },
 "is_first_login" : 0,
 "is_super_user" : 1,
 "is_update_pwd" : 0,
 "last_logged_ip" : "192.168.41.131",
 "last_logged_time" : 1502960822,
 "message" : "success",
 "nick_name" : "游客",
 "portal_id" : 28,
 "portal_url" : "http://webclient.homed.me/application/homedPortal/index.php?bsID=10",
 "property" : 0,
 "ret" : 0,
 "ret_msg" : "success",
 "style_id" : 1,
 "user_id" : 50312579,
 "user_name" : "guest_9EBBE93A48D913636B7BD0BDEDC99F41"
 }
 */
public class LoginInfo extends BaseResponse implements Serializable{

    @Expose
    private String access_token;

    @Expose
    private String area_code;

    @Expose
    private long device_id;

    @Expose
    private int home_id;
    @Expose
    private IconUrl icon_url;
    @Expose
    private int is_first_login;
    @Expose
    private int is_super_user;
    @Expose
    private String nick_name;
    @Expose
    private int portal_id;
    @Expose
    private String portal_url;
    @Expose
    private int property;
    @Expose
    private int style_id;
    @Expose
    private String user_id;
    @Expose
    private String user_name;
    @Expose
    private int left_msg_count;

    public String last_logged_ip;//上次登录ip

    public String last_logged_time;//上次登录时间

    public int is_update_pwd;//是否需要更新密码，0：不需要更新，1：超过时长，建议更换密码；2：出现密码多次输入失败被锁定情况，建议更换密码

    public String geticon()
    {
        String result = null;
        String[] dataStrings = icon_url.toString().substring(1, icon_url.toString().length()-1).split(":");
        if(dataStrings.length>=2)
            result = "http:"+dataStrings[1];
        System.out.println("result : "+result);
        return result;
    }

    public int getHome_id() {
        return home_id;
    }

    public void setHome_id(int home_id) {
        this.home_id = home_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getIs_super_user() {
        return is_super_user;
    }

    public void setIs_super_user(int is_super_user) {
        this.is_super_user = is_super_user;
    }

    public IconUrl getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(IconUrl icon_url) {
        this.icon_url = icon_url;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public int getIs_first_login() {
        return is_first_login;
    }

    public void setIs_first_login(int is_first_login) {
        this.is_first_login = is_first_login;
    }

    public long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getPortal_id() {
        return portal_id;
    }

    public void setPortal_id(int portal_id) {
        this.portal_id = portal_id;
    }

    public String getPortal_url() {
        return portal_url;
    }

    public void setPortal_url(String portal_url) {
        this.portal_url = portal_url;
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public int getStyle_id() {
        return style_id;
    }

    public void setStyle_id(int style_id) {
        this.style_id = style_id;
    }

    public int getLeft_msg_count() {
        return left_msg_count;
    }

    public void setLeft_msg_count(int left_msg_count) {
        this.left_msg_count = left_msg_count;
    }

}
