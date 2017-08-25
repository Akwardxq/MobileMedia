package com.kegy.mobilemedia.utils.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kegy.mobilemedia.utils.Logger;
import com.squareup.otto.Bus;

/**
 * Created by kegy on 2017/8/12.
 */

public class OttoUtils {

    private static final Bus BUS = new Bus();

    public static Bus getBus() {
        return BUS;
    }

    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 0)
                    BUS.post(msg.obj);
            } catch (Exception e) {
                Logger.d("OttoUtils post message exception " + e);
                e.printStackTrace();
            }
        }

    };

    public static void postOnUiThread(Object obj) {
        UI_HANDLER.obtainMessage(0, obj).sendToTarget();
    }

    /**
     * 通知AudioPlayerActivity更新UI的事件
     */
    public static final class NotifyAudioFetchEvent {
    }

    /**
     * 通知音乐顺序列表音乐播放完成事件
     */
    public static final class NotifyAudioPlayOverEvent {
    }

}
