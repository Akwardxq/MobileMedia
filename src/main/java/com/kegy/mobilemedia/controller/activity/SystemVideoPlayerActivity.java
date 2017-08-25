package com.kegy.mobilemedia.controller.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.base.BaseVideoPlayerActivity;
import com.kegy.mobilemedia.controller.view.VideoView;
import com.kegy.mobilemedia.controller.window.ChooseSeriesWindow;
import com.kegy.mobilemedia.model.account.SeriesInfoList;
import com.kegy.mobilemedia.model.account.VideoDetail;
import com.kegy.mobilemedia.model.media.MediaItem;
import com.kegy.mobilemedia.model.media.SerializableList;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.Logger;
import com.kegy.mobilemedia.utils.Toaster;
import com.kegy.mobilemedia.utils.common.Icon;
import com.kegy.mobilemedia.utils.common.NetUtils;
import com.kegy.mobilemedia.utils.common.TimeUtils;
import com.kegy.mobilemedia.utils.http.HttpUtils;
import com.kegy.mobilemedia.utils.manager.APIManager;
import com.kegy.mobilemedia.utils.manager.MobileDataManager;

import java.io.Serializable;
import java.util.List;


/**
 * 播放视频的类，播放的视频源可能有如下几种情况：
 * 1.单个本地文件
 * 2.多个本地文件
 * 3.其他应用启动该activity传的URI
 * 4.网络视频
 */
public class SystemVideoPlayerActivity extends BaseVideoPlayerActivity {
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_FULL_SCREEN = 2;

    private static final int HIDE_CONTROLLER_TIME = 4 * 1000;

    //stands for other app starts our application
    private static final String LOCAL_MEDIA_LIST = "local_media";
    private static final String NET_MEDIA = "net_media";

    private static final String EXTRA_PATH = "play_path";
    private static final String EXTRA_MEDIAS = "media_list";
    private static final String EXTRA_RES_TYPE = "res_type";
    private static final String EXTRA_PLAY_INDEX = "play_index";

    private static final int REFRESH_MESSAGE = 0x120;
    private static final int PLAY_NEXT_MESSAGE = 0x121;
    private static final int TOGGLE_CONTROLLER_MESSAGE = 0x122;
    private static final int REFRESH_NETWORK_MESSAGE = 0x123;

    private String mResType;

    private ResourceType mResourceType = ResourceType.TYPE_SINGLE;

    private enum ResourceType {
        TYPE_SINGLE, TYPE_LIST, TYPE_NETWORK, TYPE_OTHERS
    }

    private VideoView mVideoPlayer;
    private TextView mMediaName, mCurrentTime, mCurrentDuration, mTotalDuration;
    private ImageView mBatteryInfo;
    private Button mVolumeBtn, mSwitchBtn, mBackBtn, mPreBtn, mFullScnBtn;
    private TextView mNext, mPlay;
    private SeekBar mVolumeSkb, mMediaSkb;

    private AudioManager mAudioManager;

    private String mPath;

    private BatteryReceiver mBatteryReceiver;

    private int mCurrentPlayIndex;

    private List<MediaItem> mMediaItems;

    private List<SeriesInfoList.SeriesInfoListItem> mNetMediaItems;

    private GestureDetector mGestureDetector;

    private View mMediaController;

    private int mVideoWidth, mVideoHeight;

    //是否全屏播放
    private boolean mIsFullScreen = false;

    private boolean mIsMute = false;

    private int mCurrentVolume, mMaxVolume;

    private int mTouchSlop;

    private int mScreenWidth, mScreenHeight;

    private View mMessageView;
    private TextView mMessageType, mMessageContent;

    private View mScrollMessageView;
    private ImageView mScrollType;
    private TextView mScrollContent;

    private View mBufferView;
    private TextView mBufferTextView;

    private View mLoadingView;
    private TextView mLoadingTextView;

    private Uri mUri;

    private boolean mUseSystemInfo = false;

    private ClickListener mClickListener = new ClickListener();
    private SeekBarChangeListener mSeekBarChangeListener = new SeekBarChangeListener();

    private int mPrePosition;

    //清晰度和选集看
    private TextView mPlayRate, mSelectVideo;
//    private GridView mAllVideos;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_MESSAGE:
                    mCurrentDuration.setText(TimeUtils.toTimeStr(mVideoPlayer.getCurrentPosition()));
                    mMediaSkb.setProgress(mVideoPlayer.getCurrentPosition());
                    mCurrentTime.setText(TimeUtils.currentTimeStr());
                    if (mNetUri) {
                        int percentage = mVideoPlayer.getBufferPercentage();
                        int buffer = percentage * mMediaSkb.getMax();
                        int secondaryProgress = buffer / 100;
                        mMediaSkb.setSecondaryProgress(secondaryProgress);
                    } else {
                        mMediaSkb.setSecondaryProgress(0);
                    }

                    if (!mUseSystemInfo && mVideoPlayer.isPlaying()) {
                        int distance = mVideoPlayer.getCurrentPosition() - mPrePosition;
                        if (distance < 500) {
                            String netSpeed = NetUtils.getNetSpeed(SystemVideoPlayerActivity.this);
                            mBufferView.setVisibility(View.VISIBLE);
                            mBufferTextView.setText("缓冲中..." + netSpeed);
                        } else {
                            mBufferView.setVisibility(View.GONE);
                        }
                    }

                    mPrePosition = mVideoPlayer.getCurrentPosition();

                    mHandler.removeMessages(REFRESH_MESSAGE);
                    mHandler.sendEmptyMessageDelayed(REFRESH_MESSAGE, 1000);
                    break;
                case PLAY_NEXT_MESSAGE:
                    playBy(1);
                    break;
                case TOGGLE_CONTROLLER_MESSAGE:
                    toggleControllerVisibility();
                    break;
                case REFRESH_NETWORK_MESSAGE:
                    String netSpeed = NetUtils.getNetSpeed(SystemVideoPlayerActivity.this);
                    mLoadingTextView.setText("正在玩命加载中..." + netSpeed);
                    mBufferTextView.setText("缓冲中..." + netSpeed);

                    mHandler.removeMessages(REFRESH_NETWORK_MESSAGE);
                    mHandler.sendEmptyMessageDelayed(REFRESH_NETWORK_MESSAGE, 2000);
                    break;
            }
        }
    };
    private boolean mNetUri;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
        return intent;
    }

    /**
     * 传递单个播放文件（不一定是本地资源）
     *
     * @param context
     * @param path
     * @return
     */
    public static Intent newIntent(Context context, String path) {
        Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        return intent;
    }

    /**
     * 传递多个播放文件，但还是本地的资源
     *
     * @param context
     * @param serializable
     * @param index
     * @return
     */
    public static Intent newIntent(Context context, Serializable serializable, int index) {
        Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
        intent.putExtra(EXTRA_RES_TYPE, LOCAL_MEDIA_LIST);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MEDIAS, serializable);
        bundle.putInt(EXTRA_PLAY_INDEX, index);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * 传递的是多个播放文件，但是是网络的视频资源，不传递playindex，默认被赋值为0
     *
     * @param context
     * @param serializable
     * @return
     */
    public static Intent newIntent(Context context, Serializable serializable) {
        Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
        intent.putExtra(EXTRA_RES_TYPE, NET_MEDIA);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MEDIAS, serializable);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("VideoPlayer onCreate");
        setContentView(R.layout.activity_system_video_player);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Config.sDeviceWidth = displayMetrics.widthPixels;
        Config.sDeviceHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;

        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        initViews();
        initEvent();
        registerReceivers();
        initVolume();
        setMediaListener();
        getData();
    }

    private void initVolume() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeSkb.setMax(mMaxVolume);
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumeSkb.setProgress(mCurrentVolume);
    }

    private void registerReceivers() {
        mBatteryReceiver = new BatteryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, intentFilter);
    }

    private void setMediaListener() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            mVideoPlayer.setOnInfoListener(new InfoListener());
//            mUseSystemInfo = true;
//        }
        mVideoPlayer.setOnPreparedListener(new PreparedListener());
        mVideoPlayer.setOnErrorListener(new ErrorListener());
        mVideoPlayer.setOnCompletionListener(new CompletionListener());
    }

    /**
     * get extra data
     */
    private void getData() {
        mResType = getIntent().getStringExtra(EXTRA_RES_TYPE);
        Logger.d("getData res type: " + mResType);

        mUri = getIntent().getData();
        if (mUri != null)
            mResourceType = ResourceType.TYPE_OTHERS;

        mPath = getIntent().getStringExtra(EXTRA_PATH);
        if (mPath != null)
            mResourceType = ResourceType.TYPE_SINGLE;

        if (NET_MEDIA.equals(getIntent().getStringExtra(EXTRA_RES_TYPE))) {
            mResourceType = ResourceType.TYPE_NETWORK;
        }

        if (LOCAL_MEDIA_LIST.equals(getIntent().getStringExtra(EXTRA_RES_TYPE))) {
            mResourceType = ResourceType.TYPE_LIST;
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (mResourceType == ResourceType.TYPE_LIST) {
                SerializableList<MediaItem> list = (SerializableList<MediaItem>)
                        bundle.getSerializable(EXTRA_MEDIAS);
                if (list != null) {
                    mMediaItems = list.getList();
                    mCurrentPlayIndex = bundle.getInt(EXTRA_PLAY_INDEX);
                    mResourceType = ResourceType.TYPE_LIST;
                }
            }
            if (mResourceType == ResourceType.TYPE_NETWORK) {
                SerializableList<SeriesInfoList.SeriesInfoListItem> list =
                        (SerializableList<SeriesInfoList.SeriesInfoListItem>)
                                bundle.getSerializable(EXTRA_MEDIAS);
                if (list != null) {
                    mNetMediaItems = list.getList();
                    mCurrentPlayIndex = 0;
                    mResourceType = ResourceType.TYPE_NETWORK;
                }
            }
        }
        setData();
    }

    private void setData() {
        switch (mResourceType) {
            case TYPE_SINGLE:
                Logger.d("path is not null " + mPath);
                mNetUri = NetUtils.isNetResource(mPath);
                mVideoPlayer.setVideoPath(mPath);
                break;
            case TYPE_LIST:
                MediaItem mediaItem = mMediaItems.get(mCurrentPlayIndex);
                if (mediaItem != null) {
                    Logger.d("mediaitem is not null");
                    mNetUri = NetUtils.isNetResource(mediaItem.getPath());
                    mVideoPlayer.setVideoPath(mediaItem.getPath());
                }
                break;
            case TYPE_OTHERS:
                Logger.d("uri is not null " + mUri);
                mNetUri = NetUtils.isNetResource(mUri.toString());
                mVideoPlayer.setVideoURI(mUri);
                break;
            case TYPE_NETWORK:
                Logger.d("type network");
                SeriesInfoList.SeriesInfoListItem item = mNetMediaItems.get(mCurrentPlayIndex);
                if (item != null) {
                    getMovieSeriesPlayInfo(item.getVideo_id());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取某个视频的具体信息
     */
    private void getMovieSeriesPlayInfo(final String netResVideoId) {
        Logger.d("getMovieSeriesPlayInfo videoid: " + netResVideoId);
        if (TextUtils.isEmpty(netResVideoId)) {
            Toaster.toast(this, "加载视频信息失败");
            return;
        }
        APIManager.getVideoInfo(netResVideoId, new HttpUtils.StringResponseListener() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Logger.d("getMovieSeriesPlayInfo success");
                    VideoDetail video = MobileDataManager.getGson().fromJson(result, VideoDetail.class);
                    playMovieSeriesItem(video
                            .getDemand_url().get(0), video
                            .getPlay_token(), netResVideoId);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private String mPlayUrl;

    private void playMovieSeriesItem(String url, String playtoken, String netResVideoId) {
        if (TextUtils.isEmpty(url))
            return;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("playtype", "demand");
        builder.appendQueryParameter("protocol", "http");
        builder.appendQueryParameter("accesstoken", MobileDataManager.getAccessToken());
        builder.appendQueryParameter("programid", netResVideoId);
        builder.appendQueryParameter("playtoken", playtoken);
        builder.appendQueryParameter("verifycode", "" + 33); // 33
        // verifycode
        builder.appendQueryParameter("rate", "normal");
        String playUrl = builder.build().toString();
        this.mPlayUrl = playUrl;
        Logger.d("playurl:  " + playUrl);
        mVideoPlayer.stopPlayback();
        mVideoPlayer.setVideoPath(playUrl);
    }

    private void toggleVideoViewSize() {
        if (mIsFullScreen) {
            setVideoViewSize(TYPE_NORMAL);
        } else {
            setVideoViewSize(TYPE_FULL_SCREEN);
        }
    }

    private void setVideoViewSize(int type) {
        switch (type) {
            case TYPE_NORMAL:
                mIsFullScreen = false;
                mFullScnBtn.setBackgroundResource(R.drawable.ib_fullscreen_selector);
//                int width = Config.sDeviceWidth;
//                int height = Config.sDeviceHeight;
                int width = mScreenWidth;
                int height = mScreenHeight;
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                mVideoPlayer.setVideoSize(width, height);
                break;
            case TYPE_FULL_SCREEN:
                mIsFullScreen = true;
                mVideoPlayer.setVideoSize(Config.sDeviceWidth, Config.sDeviceHeight);
                mFullScnBtn.setBackgroundResource(R.drawable.ib_not_fullscreen_selector);
                break;
        }
    }

    private void togglePlayState() {
        if (mVideoPlayer.isPlaying()) {
            mVideoPlayer.pause();
//            mPlayBtn.setBackgroundResource(R.drawable.ib_pause_selector);
            mPlay.setText(getResources().getString(R.string.icon_videoview_play));
            Icon.applyTypeface(mPlay);
        } else {
            mVideoPlayer.start();
//            mPlayBtn.setBackgroundResource(R.drawable.ib_playing_selector);
            mPlay.setText(getResources().getString(R.string.icon_videoview_pause));
            Icon.applyTypeface(mPlay);
        }
    }

    private void toggleControllerVisibility() {
//        mAllVideos.setVisibility(View.GONE);
        if (mMediaController.getVisibility() == View.VISIBLE) {
            mMediaController.setVisibility(View.INVISIBLE);
            mHandler.removeMessages(TOGGLE_CONTROLLER_MESSAGE);
        } else {
            mMediaController.setVisibility(View.VISIBLE);
            mHandler.removeMessages(TOGGLE_CONTROLLER_MESSAGE);
            mHandler.sendEmptyMessageDelayed(TOGGLE_CONTROLLER_MESSAGE, HIDE_CONTROLLER_TIME);
        }
    }

    private void initViews() {
        mMessageView = findViewById(R.id.rl_message);
        mMessageType = (TextView) findViewById(R.id.tv_message_type);
        mMessageContent = (TextView) findViewById(R.id.tv_message_content);

        mScrollMessageView = findViewById(R.id.rl_message1);
        mScrollType = (ImageView) findViewById(R.id.iv_scroll_type);
        mScrollContent = (TextView) findViewById(R.id.tv_scroll_content);

        mBufferView = findViewById(R.id.rl_buffer);
        mBufferTextView = (TextView) findViewById(R.id.tv_buffer);

        mLoadingView = findViewById(R.id.rl_loading);
        mLoadingTextView = (TextView) findViewById(R.id.tv_loading);

        mVideoPlayer = (VideoView) findViewById(R.id.video_player_view);
        mMediaController = findViewById(R.id.media_controller);
        mMediaName = (TextView) findViewById(R.id.tv_controller_display_name);
        mBatteryInfo = (ImageView) findViewById(R.id.iv_controller_battery);
        mCurrentTime = (TextView) findViewById(R.id.tv_controller_current_time);
        mVolumeBtn = (Button) findViewById(R.id.ib_voice_silent);
        mVolumeSkb = (SeekBar) findViewById(R.id.sb_voice);
        mSwitchBtn = (Button) findViewById(R.id.ib_switch);
        mCurrentDuration = (TextView) findViewById(R.id.tv_current_duration);
        mMediaSkb = (SeekBar) findViewById(R.id.sb_movie);
        mTotalDuration = (TextView) findViewById(R.id.tv_total_duration);
        mBackBtn = (Button) findViewById(R.id.ib_exit);
        mPreBtn = (Button) findViewById(R.id.ib_previous);
        mPlay = (TextView) findViewById(R.id.ib_playing);
        mNext = (TextView) findViewById(R.id.ib_next);
        mFullScnBtn = (Button) findViewById(R.id.ib_fullscreen);
        mPlayRate = (TextView) findViewById(R.id.tv_play_rate);
        mPlayRate.setOnClickListener(mClickListener);
        mSelectVideo = (TextView) findViewById(R.id.tv_select_video);
        mSelectVideo.setOnClickListener(mClickListener);
//        mAllVideos = (GridView) findViewById(R.id.lv_all_videos);
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                toggleVideoViewSize();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                togglePlayState();
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggleControllerVisibility();
                return false;
            }
        });

        mHandler.sendEmptyMessage(REFRESH_NETWORK_MESSAGE);

    }

    private void initEvent() {
        mVolumeBtn.setOnClickListener(mClickListener);
        mVolumeSkb.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mMediaSkb.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSwitchBtn.setOnClickListener(mClickListener);
        mBackBtn.setOnClickListener(mClickListener);
        mPreBtn.setOnClickListener(mClickListener);
        mPlay.setOnClickListener(mClickListener);
        Icon.applyTypeface(mPlay);
        mNext.setOnClickListener(mClickListener);
        Icon.applyTypeface(mNext);
        mFullScnBtn.setOnClickListener(mClickListener);
    }

    private void setBattery(int level) {
        if (level <= 0) {
            mBatteryInfo.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            mBatteryInfo.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            mBatteryInfo.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            mBatteryInfo.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            mBatteryInfo.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            mBatteryInfo.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            mBatteryInfo.setImageResource(R.drawable.ic_battery_100);
        }
    }

    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBattery(level);
        }
    }

    @Override
    protected void onDestroy() {
        if (mBatteryReceiver != null) {
            unregisterReceiver(mBatteryReceiver);
            mBatteryReceiver = null;
        }
        if (mVideoPlayer != null) {
            mVideoPlayer.stopPlayback();
            mVideoPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == mMediaSkb) {
                if (fromUser) {
                    mVideoPlayer.seekTo(progress);
                }
            }
            if (seekBar == mVolumeSkb) {
                if (fromUser) {
                    updateVolume(progress, false);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(TOGGLE_CONTROLLER_MESSAGE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(TOGGLE_CONTROLLER_MESSAGE);
            mHandler.sendEmptyMessageDelayed(TOGGLE_CONTROLLER_MESSAGE, HIDE_CONTROLLER_TIME);
        }
    }

    private void updateVolume(int progress, boolean isMute) {
        if (isMute) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            mVolumeSkb.setProgress(progress);
        } else {
            mCurrentVolume = progress;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,
                    0);
            mVolumeSkb.setProgress(progress);
        }
    }

    private void updateBrightness(float bright) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = bright;
        getWindow().setAttributes(layoutParams);
        mMessageView.setVisibility(View.VISIBLE);
        mMessageType.setText("亮度");
        mMessageContent.setText("" + "" + (int) (layoutParams.screenBrightness));
    }

    @Override
    public void playTo(int temp) {
        mLoadingView.setVisibility(View.VISIBLE);
        switch (mResourceType) {
            case TYPE_LIST:
                if (temp < 0) {
                    temp = mMediaItems.size() - 1;
                }
                if (temp > mMediaItems.size() - 1) {
                    temp = 0;
                }
                mCurrentPlayIndex = temp;
                mVideoPlayer.stopPlayback();
                mNetUri = NetUtils.isNetResource(mMediaItems.get(mCurrentPlayIndex).getPath());
                mVideoPlayer.setVideoPath(mMediaItems.get(mCurrentPlayIndex).getPath());
                break;
            case TYPE_NETWORK:
                if (temp < 0) {
                    temp = mNetMediaItems.size() - 1;
                }
                if (temp > mNetMediaItems.size() - 1) {
                    temp = 0;
                }
                mCurrentPlayIndex = temp;
                getMovieSeriesPlayInfo(mNetMediaItems.get(mCurrentPlayIndex).getVideo_id());
                break;
            default:
                break;
        }
    }

    @Override
    public void playBy(int direction) {
        int temp = mCurrentPlayIndex + direction;
        playTo(temp);
    }

    class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_voice_silent:
                    mIsMute = !mIsMute;
                    if (mIsMute) {
                        updateVolume(0, true);
                    } else {
                        updateVolume(mCurrentVolume, false);
                    }
                    break;
                case R.id.ib_exit:
                    onBackPressed();
                    break;
                case R.id.ib_previous:
                    playBy(-1);
                    break;
                case R.id.ib_playing:
                    togglePlayState();
                    break;
                case R.id.ib_next:
                    playBy(1);
                    break;
                case R.id.ib_fullscreen:
                    toggleVideoViewSize();
                    break;
                case R.id.ib_switch:
                    showSwitchDialog();
                    break;
                case R.id.tv_select_video:
                    showChooseSeriesWindow();
//                    showAllVideos();
                    break;
            }
            mHandler.removeMessages(TOGGLE_CONTROLLER_MESSAGE);
            mHandler.sendEmptyMessageDelayed(TOGGLE_CONTROLLER_MESSAGE, HIDE_CONTROLLER_TIME);
        }
    }

    private ChooseSeriesWindow mChooseSeriesWindow;

    public void showChooseSeriesWindow() {
        if (mResourceType == ResourceType.TYPE_NETWORK) {
            mChooseSeriesWindow = new ChooseSeriesWindow(this, mNetMediaItems);
            mChooseSeriesWindow.show(mVideoPlayer, mNetMediaItems.get(mCurrentPlayIndex).getSeries_idx());
            toggleControllerVisibility();
        }
    }

    private void showSwitchDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提示")
                .setMessage("当您播放视频有声音没图像的时候，点击切换按钮尝试切换万能播放器播放")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startVitamioPlayer();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    class InfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡住了
                    mBufferView.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频缓冲结束了
                    mBufferView.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    }

    class PreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            setVideoViewSize(TYPE_NORMAL);
            mp.start();
            mLoadingView.setVisibility(View.INVISIBLE);
            mMediaSkb.setMax(mVideoPlayer.getDuration());
            mTotalDuration.setText(TimeUtils.toTimeStr(mVideoPlayer.getDuration()));
            mHandler.sendEmptyMessageDelayed(REFRESH_MESSAGE, 1000);
            if (mUri != null) {
                mMediaName.setText(mUri.toString());
            } else if (mMediaItems != null) {
                if (mMediaItems.get(mCurrentPlayIndex) != null)
                    mMediaName.setText(mMediaItems.get(mCurrentPlayIndex).getDisplayName());
            } else if (!TextUtils.isEmpty(mPath)) {
                mMediaName.setText(mPath);
            }
        }
    }

    class ErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            startVitamioPlayer();
            return true;
        }
    }

    class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mHandler.sendEmptyMessageDelayed(PLAY_NEXT_MESSAGE, 500);
        }

    }

    private void startVitamioPlayer() {
        Intent intent = null;
        if (mMediaItems != null) {
            SerializableList<MediaItem> list = new SerializableList<>();
            list.setList(mMediaItems);
            intent = VitamioVideoPlayerActivity.newIntent(this, list, mCurrentPlayIndex);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        } else if (mUri != null) {
            intent = VitamioVideoPlayerActivity.newIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setData(mUri);
        } else if (mNetMediaItems != null) {
            SerializableList<SeriesInfoList.SeriesInfoListItem> itemSerializableList = new SerializableList<>();
            itemSerializableList.setList(mNetMediaItems);
            intent = VitamioVideoPlayerActivity.newIntent(this, itemSerializableList);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        } else {
            intent = VitamioVideoPlayerActivity.newIntent(this,mPath);
        }
        startActivity(intent);
        finish();
    }

    private float mDownX, mDownY;
    private int mOperation;
    private int mVol;
    private int mTouchRang;
    private float mBright;
    private float mOriginalBright = -1f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Logger.d("SystemVideoPlayerActivity onTouchEvent");
        if (mChooseSeriesWindow != null)
            mChooseSeriesWindow.hide();
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOperation = 0;
                mDownX = event.getX();
                mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mTouchRang = Math.min(mScreenHeight, mScreenWidth);
                mDownY = event.getY();
                mBright = mOriginalBright;
                if (mBright < 0.01f) {
                    int value = 0;
                    try {
                        value = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                        mBright = value / 255f;
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Logger.d("onTouch down brightness value: " + mBright);
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - mDownX;
                float deltaY = event.getY() - mDownY;
                if (Math.abs(deltaX) > mTouchSlop || Math.abs(deltaY) > mTouchSlop) {
                    if (mOperation == 0) {
                        if (Math.abs(deltaY) > Math.abs(deltaX)) {//上下滑动
                            if (mDownX >= mScreenWidth / 2) {//右边
                                mOperation = 1;
                            } else {
                                mOperation = 2;
                            }
                        } else {
                            mOperation = 3;
                        }
                    } else if (mOperation == 1) {//音量
                        float percent = ((-deltaY) / mScreenHeight);
                        int delta = (int) (mMaxVolume * percent);
                        int vol = Math.min(Math.max(mVol + delta, 0), mMaxVolume);
                        onVolumeSlide(vol);
                    } else if (mOperation == 2) {//亮度
                        float percent = ((-deltaY) / mScreenHeight);
                        float bright = mBright + percent;
                        onBrightnessSlide(bright);
                    } else {//快进快退
                        if (deltaX > 0) {
                            float percent = deltaX / mScreenWidth;
                            fastSpeed(percent);
                        } else {
                            float percent = deltaX / mScreenWidth;
                            backSpeed(percent);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                dismissMessage();
                mOperation = 0;
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mCurrentVolume--;
                mHandler.removeMessages(TOGGLE_CONTROLLER_MESSAGE);
                mHandler.sendEmptyMessageDelayed(TOGGLE_CONTROLLER_MESSAGE, HIDE_CONTROLLER_TIME);
                updateVolume(mCurrentVolume, false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                mCurrentVolume++;
                mHandler.removeMessages(TOGGLE_CONTROLLER_MESSAGE);
                mHandler.sendEmptyMessageDelayed(TOGGLE_CONTROLLER_MESSAGE, HIDE_CONTROLLER_TIME);
                updateVolume(mCurrentVolume, false);
                return true;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }

    private void fastSpeed(float percent) {
        Logger.d("fastSpeed percent: " + percent);
        long fast = (long) (mVideoPlayer.getDuration() * percent);
        long current = Math.min(mVideoPlayer.getCurrentPosition() + fast, mVideoPlayer.getDuration());
        mScrollMessageView.setVisibility(View.VISIBLE);
        mScrollType.setImageResource(R.drawable.right_arrow);
        mScrollContent.setText(TimeUtils.toTimeStr(current));
//        mVideoPlayer.seekTo((int) current);
    }

    private void backSpeed(float percent) {
        Logger.d("backSpeed percent: " + percent);
        long fast = (long) (mVideoPlayer.getDuration() * percent);
        long current = Math.max(mVideoPlayer.getCurrentPosition() + fast, 0);
        mScrollMessageView.setVisibility(View.VISIBLE);
        mScrollType.setImageResource(R.drawable.left_arrow);
        mScrollContent.setText(TimeUtils.toTimeStr(current));
        mVideoPlayer.seekTo((int) current);
    }

    private void dismissMessage() {
        mMessageView.setVisibility(View.GONE);
        mMessageType.setText("");
        mMessageContent.setText("");
        mScrollMessageView.setVisibility(View.GONE);
        mScrollContent.setText("");
    }

    private void onVolumeSlide(int vol) {
        updateVolume(vol, false);
        mMessageView.setVisibility(View.VISIBLE);
        mMessageType.setText("音量");
        mMessageContent.setText("" + vol);
    }

    private void onBrightnessSlide(float bright) {
        Logger.d("onBrightnessSlide bright: " + bright);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = bright;
        if (layoutParams.screenBrightness >= 1.0f) {
            layoutParams.screenBrightness = 1.0f;
        }
        if (layoutParams.screenBrightness <= 0.01f) {
            layoutParams.screenBrightness = 0.01f;
        }
        getWindow().setAttributes(layoutParams);
        mOriginalBright = getWindow().getAttributes().screenBrightness;
        mMessageView.setVisibility(View.VISIBLE);
        mMessageType.setText("亮度");
        mMessageContent.setText("" + "" + (int) (layoutParams.screenBrightness * 100));
    }

}
