package com.kegy.mobilemedia;

import android.app.Application;

import com.kegy.mobilemedia.model.account.LoginInfo;
import com.kegy.mobilemedia.model.account.TypeList;
import com.kegy.mobilemedia.utils.user.Tourist;

/**
 * Created by Administrator on 2017/8/24.
 */

public class MediaApp extends Application {

    public static LoginInfo sLoginInfo;
    public static TypeList.TypeChildren sTypeChildren;

    @Override
    public void onCreate() {
        super.onCreate();
//        Tourist.getInstance(this).login();
    }

    public static void setLoginInfo(LoginInfo loginInfo) {
        sLoginInfo = loginInfo;
    }

    public static void setTypeChildren(TypeList.TypeChildren typeChildren) {
        sTypeChildren = typeChildren;
    }

    public static TypeList.TypeChildren getTypeChildrenById(int id) {
        if (sTypeChildren != null) {
            for (TypeList.TypeChildren children : sTypeChildren.getChildren()) {
                if (children.getId() == id)
                    return children;
            }
        }
        return null;
    }
}
