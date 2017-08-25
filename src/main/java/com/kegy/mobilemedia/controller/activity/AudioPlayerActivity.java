package com.kegy.mobilemedia.controller.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.service.AudioPlayerService;
import com.kegy.mobilemedia.controller.view.LyricShowView;
import com.kegy.mobilemedia.utils.common.LyricUtils;
import com.kegy.mobilemedia.utils.common.OttoUtils;
import com.kegy.mobilemedia.utils.common.TimeUtils;
import com.kegy.mobilevideo.IAudioPlayerService;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.Serializable;

public class AudioPlayerActivity extends Activity {
    private static final String TAG = "AudioPlayerActivity";

    private static final String EXTRA_POSITION = "position";
    private static final String EXTRA_AUDIOS = "audio_list";

    private static final int REFRESH_MESSAGE = 0x120;
    private static final int REFRESH_DELAY = 1000;
    private static final int SHOW_LYRIC_MESSAGE = 0x121;

    private static final int[] PLAY_MODES_IDS = {
            R.drawable.btn_audio_playmode_normal_selector,
            R.drawable.btn_audio_playmode_single_selector,
            R.drawable.btn_audio_playmode_all_selector
    };

    private int mCurrentPlayMode = 0;//默认播放模式为正常模式

    private ImageView mPlayingMatrix;
    private TextView mSongName, mArtist;
    private TextView mCurrentPosition;
    private SeekBar mAudioSkb;
    private Button mPlayModeBtn, mPreBtn, mPlayPauseBtn, mNextBtn, mLyricBtn;

    private int mPlayIndex;
    private IAudioPlayerService mService;
    private LyricShowView mLyricShowView;

    private ClickListener mClickListener = new ClickListener();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_MESSAGE:
                    try {
                        mAudioSkb.setProgress(mService.getCurrentPosition());
                        mCurrentPosition.setText(TimeUtils.toTimeStr(mService.getCurrentPosition()) +
                                "/" + TimeUtils.toTimeStr(mService.getDuration()));
                        mLyricShowView.postInvalidate();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessageDelayed(REFRESH_MESSAGE, REFRESH_DELAY);
                    break;
                case SHOW_LYRIC_MESSAGE:
                    if (mService != null) {
                        try {
                            int currentPosition = mService.getCurrentPosition();
                            mLyricShowView.setShowLyric(currentPosition);
                            mHandler.removeMessages(SHOW_LYRIC_MESSAGE);
                            mHandler.sendEmptyMessage(SHOW_LYRIC_MESSAGE);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mService = IAudioPlayerService.Stub.asInterface(iBinder);
            if (mService != null)
                try {
                    if (!mFromNotification) {
                        mService.playByIndex(mPlayIndex);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateAudioInfo();
                            }
                        });
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (mService != null) {
                    mService.stop();
                    mService = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public static Intent newIntent(Context context, int position, Serializable serializable) {
        Intent intent = new Intent(context, AudioPlayerActivity.class);
        intent.putExtra(EXTRA_POSITION, position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_AUDIOS, serializable);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        OttoUtils.getBus().register(this);
        initViews();
        getData();
        bindAudioService();
    }

    private void initViews() {
        mPlayingMatrix = (ImageView) findViewById(R.id.iv_now_playing_matrix);
        AnimationDrawable animationDrawable = (AnimationDrawable) mPlayingMatrix.getBackground();
        animationDrawable.start();
        mSongName = (TextView) findViewById(R.id.tv_now_playing_song_name);
        mArtist = (TextView) findViewById(R.id.tv_now_playing_singer);
        mCurrentPosition = (TextView) findViewById(R.id.tv_now_playing_time);
        mAudioSkb = (SeekBar) findViewById(R.id.sb_audio);
        mPlayModeBtn = (Button) findViewById(R.id.btn_audio_playmode);
        mPreBtn = (Button) findViewById(R.id.btn_audio_previous);
        mPlayPauseBtn = (Button) findViewById(R.id.btn_audio_playing_pause);
        mNextBtn = (Button) findViewById(R.id.btn_audio_next);
        mLyricBtn = (Button) findViewById(R.id.btn_audio_lyrc);
        mPlayModeBtn.setOnClickListener(mClickListener);
        mPreBtn.setOnClickListener(mClickListener);
        mPlayPauseBtn.setOnClickListener(mClickListener);
        mNextBtn.setOnClickListener(mClickListener);
        mLyricBtn.setOnClickListener(mClickListener);
        mLyricShowView = (LyricShowView) findViewById(R.id.lyric_view);
        mAudioSkb.setOnSeekBarChangeListener(new AudioSeekChangeListener());
    }

    private boolean mFromNotification = false;

    private void getData() {
        mFromNotification = getIntent().getBooleanExtra("notification", false);
        if (!mFromNotification)
            mPlayIndex = getIntent().getIntExtra(EXTRA_POSITION, 0);
    }

    private void bindAudioService() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.setAction("com.kegy.mobilevideo.OPEN_AUDIO");
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
        startService(intent);
    }

    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_audio_playmode://设置播放模式
                    if (mService != null) {
                        try {
                            mPlayModeBtn.setBackgroundResource(
                                    PLAY_MODES_IDS[(++mCurrentPlayMode) % PLAY_MODES_IDS.length]);
                            mService.setPlayMode(mCurrentPlayMode);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_audio_previous://前一首
                    if (mService != null) {
                        try {
                            mService.playPrevious();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_audio_playing_pause://暂停音乐播放
                    if (mService != null) {
                        try {
                            if (mService.isPlaying()) {
                                mPlayPauseBtn.setBackgroundResource(R.drawable.btn_audio_start_selector);
                                mService.pause();
                            } else {
                                mPlayPauseBtn.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                                mService.start();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_audio_next:
                    if (mService != null) {
                        try {
                            mService.playNext();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_audio_lyrc:
                    break;
            }
        }
    }

    @Subscribe
    public void OnSubscribeAudioUpdateEvent(OttoUtils.NotifyAudioFetchEvent event) {
        updateAudioInfo();
    }

    @Subscribe
    public void OnSubscribeAudioPlayOverEvent(OttoUtils.NotifyAudioPlayOverEvent event) {
        mPlayPauseBtn.setBackgroundResource(R.drawable.btn_audio_start_selector);
    }

    private void updateAudioInfo() {
        if (mService != null) {
            try {
                mPlayPauseBtn.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                mSongName.setText(mService.getAudioName());
                mArtist.setText(mService.getArtist());
                mCurrentPosition.setText(TimeUtils.toTimeStr(mService.getCurrentPosition()) +
                        "/" + TimeUtils.toTimeStr(mService.getDuration()));
                mAudioSkb.setMax(mService.getDuration());
                String path = mService.getAudioPath();
                String sub = path.substring(0,path.lastIndexOf("."));
                File file = new File(sub + ".lrc");
                if (!file.exists()) {
                    file = new File(sub + ".txt");
                }
                LyricUtils lyricUtils = new LyricUtils();
                lyricUtils.readLyricFile(file);
                mLyricShowView.setLyrics(lyricUtils.getLyrics());
                mHandler.sendEmptyMessageDelayed(REFRESH_MESSAGE, REFRESH_DELAY);
                mHandler.sendEmptyMessage(SHOW_LYRIC_MESSAGE);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private class AudioSeekChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    mService.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        OttoUtils.getBus().unregister(this);
    }
}
