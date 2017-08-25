package com.kegy.mobilemedia.controller.fragment.page3;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.activity.SystemVideoPlayerActivity;
import com.kegy.mobilemedia.controller.adapter.LocalVideoAdapter;
import com.kegy.mobilemedia.controller.base.BaseFragment;
import com.kegy.mobilemedia.model.media.MediaItem;
import com.kegy.mobilemedia.model.media.SerializableList;
import com.kegy.mobilemedia.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/24.
 */

public class LocalVideoPageFragment extends BaseFragment {
    private static final String TAG = "LocalVideoPageFragment";

    private static final int LOADING_FINISHED = 0x120;

    private TextView mEmptyResult;
    private ProgressBar mLoading;
    private ListView mVideoListView;
    private List<MediaItem> mVideoItems = new ArrayList<>();
    private LocalVideoAdapter mVideoAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOADING_FINISHED) {
                Logger.d(TAG + " handleMessage loading finished");
                mLoading.setVisibility(View.INVISIBLE);
                if (mVideoItems.size() == 0) {
                    mEmptyResult.setVisibility(View.VISIBLE);
                } else {
                    mVideoListView.setVisibility(View.VISIBLE);
                    mVideoListView.setAdapter(mVideoAdapter = new LocalVideoAdapter(mVideoItems, getActivity()));
                }
            }
        }
    };

    public static LocalVideoPageFragment newInstance() {
        return new LocalVideoPageFragment();
    }

    @Override
    protected int bindContentView() {
        return R.layout.fragment_local_media_page;
    }

    @Override
    protected void initView() {
        Logger.d(TAG + " initView");
        mEmptyResult = (TextView) findViewById(R.id.tv_empty_local_medias);
        mLoading = (ProgressBar) findViewById(R.id.pb_local_medias);
        mVideoListView = (ListView) findViewById(R.id.lv_local_medias);
        mVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SerializableList<MediaItem> list = new SerializableList<>();
                list.setList(mVideoItems);
                Intent intent = SystemVideoPlayerActivity.newIntent(getActivity(), list, position);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void getData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentResolver resolver = getActivity().getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] params = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.ARTIST
                };
                Cursor cursor = resolver.query(uri, params, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        mediaItem.setDisplayName(cursor.getString(0));
                        mediaItem.setDuration(cursor.getLong(1));
                        mediaItem.setSize(cursor.getLong(2));
                        mediaItem.setPath(cursor.getString(3));
                        mediaItem.setArtist(cursor.getString(3));
                        mVideoItems.add(mediaItem);
                    }
                    mHandler.sendEmptyMessage(LOADING_FINISHED);
                    cursor.close();
                }
            }
        }).start();
    }

}
