package com.kegy.mobilemedia.controller.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;


import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.activity.AudioPlayerActivity;
import com.kegy.mobilemedia.model.media.MediaItem;
import com.kegy.mobilemedia.utils.Logger;
import com.kegy.mobilemedia.utils.common.OttoUtils;
import com.kegy.mobilevideo.IAudioPlayerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kegy on 2017/8/13.
 */

public class AudioPlayerService extends Service {
    private static final String TAG = "AudioPlayerService";

    public static final String EXTRA_AUDIOS = "audios";

    public static final int MODE_NORMAL = 0;
    public static final int MODE_SINGLE = 1;
    public static final int MODE_CYCLE = 2;

    private int mPlayIndex;
    private List<MediaItem> mMediaItems = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private MediaItem mCurrentAudio;
    private int mPlayMode = MODE_NORMAL;

    @Override
    public void onCreate() {
        super.onCreate();
        getAudioData();

    }

    private void getAudioData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] params = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor = resolver.query(uri, params, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        mediaItem.setDisplayName(cursor.getString(0));
                        mediaItem.setDuration(cursor.getLong(1));
                        mediaItem.setSize(cursor.getLong(2));
                        mediaItem.setPath(cursor.getString(3));
                        mediaItem.setArtist(cursor.getString(4));
                        mMediaItems.add(mediaItem);
                    }
                    cursor.close();
                }
            }
        }).start();
    }

    private IAudioPlayerService.Stub mStub = new IAudioPlayerService.Stub()
    {
        private AudioPlayerService mPlayerService = AudioPlayerService.this;

        @Override
        public void playByIndex(int index) throws RemoteException {
            mPlayerService.playByIndex(index);
        }

        @Override
        public void start() throws RemoteException {
            mPlayerService.start();
        }

        @Override
        public void pause() throws RemoteException {
            mPlayerService.pause();
        }

        @Override
        public void stop() throws RemoteException {
            mPlayerService.stop();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return mPlayerService.getAudioName();
        }

        @Override
        public String getArtist() throws RemoteException {
            return mPlayerService.getArtist();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return mPlayerService.getCurrentPosition();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return mPlayerService.getAudioPath();
        }

        @Override
        public int getDuration() throws RemoteException {
            return mPlayerService.getDuration();
        }

        @Override
        public void playNext() throws RemoteException {
            mPlayerService.playNext();
        }

        @Override
        public void playPrevious() throws RemoteException {
            mPlayerService.playPrevious();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            mPlayerService.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return mPlayerService.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mPlayerService.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mPlayerService.seekTo(position);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    /**
     * 播放指定index的音乐
     *
     * @param index
     */
    private void playByIndex(int index) {
        try {
            this.mPlayIndex = index;
            if (mMediaPlayer != null) {
                Logger.d("reset media player");
                mMediaPlayer.reset();
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new OnPreparedListener());
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener());
            mMediaPlayer.setOnErrorListener(new OnErrorListener());
            mCurrentAudio = mMediaItems.get(index);
            Logger.d(TAG + " play audio path: " + mCurrentAudio.getPath());
            mMediaPlayer.setDataSource(mCurrentAudio.getPath());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class OnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
            OttoUtils.getBus().post(new OttoUtils.NotifyAudioFetchEvent());
        }
    }

    private class OnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            switch (mPlayMode) {
                case MODE_NORMAL://正常模式
                    int temp = ++mPlayIndex;
                    if (temp == mMediaItems.size()) {
                        stop();
                        OttoUtils.getBus().post(new OttoUtils.NotifyAudioPlayOverEvent());
                    } else {
                        playByIndex(temp);
                    }
                    break;
                case MODE_SINGLE://单曲模式
                    playByIndex(mPlayIndex);
                    break;
                case MODE_CYCLE://循环模式
                    mPlayIndex = ((++mPlayIndex) % mMediaItems.size());
                    Logger.d("mode cycle play index: " + mPlayIndex);
                    playByIndex(mPlayIndex);
                    break;
            }
        }
    }

    private class OnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    }

    private NotificationManager mNotificationManager;
    /**
     * 开始
     */
    private void start() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent it = new Intent(this, AudioPlayerActivity.class);
        it.putExtra("notification",true);
        PendingIntent pi = PendingIntent.getActivity(this, 1, it, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("移动音乐")
                .setContentText("正在播放:" + getAudioName())
                .setContentIntent(pi)
                .build();
        mNotificationManager.notify(1,notification);
        mMediaPlayer.start();
    }

    /**
     * 暂停
     */
    private void pause() {
        mMediaPlayer.pause();
        mNotificationManager.cancel(1);
    }

    /**
     * 停止
     */
    private void stop() {
        mMediaPlayer.stop();
    }

    /**
     * 歌曲的名称
     *
     * @return
     */
    private String getAudioName() {
        return mCurrentAudio.getDisplayName();
    }

    /**
     * 歌曲的作者
     *
     * @return
     */
    private String getArtist() {
        return mCurrentAudio.getArtist();
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    private int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 获取歌曲的路径
     *
     * @return
     */
    private String getAudioPath() {
        return mCurrentAudio.getPath();
    }

    /**
     * 获取总时长
     *
     * @return
     */
    private int getDuration() {
        return (int) mCurrentAudio.getDuration();
    }

    /**
     * 播放下一个
     */
    private void playNext() {
        Logger.d("play next");
        int temp = ++mPlayIndex;
        if (temp == mMediaItems.size()) {//如果当前已经是最后一首 就跳转到第一首
            temp = 0;
        }
        playByIndex(temp);
    }

    /**
     * 播放前一首
     */
    private void playPrevious() {
        Logger.d("play previous");
        int temp = --mPlayIndex;
        if (temp < 0) {//如果当前已经是第一首 就跳到最后一首
            temp = mMediaItems.size() - 1;
        }
        playByIndex(temp);
    }

    /**
     * 设置播放模式
     */
    private void setPlayMode(int playMode) {
        this.mPlayMode = playMode;
    }

    /**
     * 获取播放模式
     *
     * @return
     */
    private int getPlayMode() {
        return mPlayMode;
    }

    private boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    private void seekTo(int position) {
        if (position <= 0) {
            position = 0;
        }
        if (position > getDuration()) {
            position = getDuration();
        }
        mMediaPlayer.seekTo(position);
    }

}
