package com.kegy.mobilemedia.controller.fragment.page1;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kegy.mobilemedia.MediaApp;
import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.activity.SystemVideoPlayerActivity;
import com.kegy.mobilemedia.controller.adapter.HomeRecommendAdapter;
import com.kegy.mobilemedia.controller.base.BaseFragment;
import com.kegy.mobilemedia.model.account.RankListResponse;
import com.kegy.mobilemedia.model.account.SeriesInfoList;
import com.kegy.mobilemedia.model.media.NetVideo;
import com.kegy.mobilemedia.model.media.SerializableList;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.Logger;
import com.kegy.mobilemedia.utils.Toaster;
import com.kegy.mobilemedia.utils.http.HttpUtils;
import com.kegy.mobilemedia.utils.manager.APIManager;
import com.kegy.mobilemedia.utils.manager.MobileDataManager;

import java.util.Collections;
import java.util.List;

/**
 * 首页推荐Fragment<br>
 *
 * @author kegy
 */
public class HomeRecommendFragment extends BaseFragment {

    private static final int LOADING_FINISHED_MESSAGE = 0x120;

    private TextView mTvNoData;
    private ListView mListView;
    private NetVideo mNetVideo;
    private HomeRecommendAdapter mAdapter;

    private int mRequestNum = 5;
    //刷新的次数
    private int mRefreshCount = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING_FINISHED_MESSAGE:
                    break;
            }
        }
    };

    @Override
    protected int bindContentView() {
        return R.layout.fragment_home_recommend;
    }

    @Override
    protected void initView() {
        mTvNoData = (TextView) findViewById(R.id.tv_no_data);
        mListView = (ListView) findViewById(R.id.lv_home_recommend);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetVideo.VideoItem videoItem = (NetVideo.VideoItem) mAdapter.getItem(position);
                Intent intent = SystemVideoPlayerActivity.newIntent(getActivity(),videoItem.hightUrl);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void getData() {
        HttpUtils.callJSONAPI(Config.NET_VIDEO_SRC, new HttpUtils.StringResponseListener() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    mNetVideo = MobileDataManager.getGson().fromJson(result,NetVideo.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListView.setAdapter(mAdapter = new HomeRecommendAdapter(mNetVideo,getActivity()));
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                mTvNoData.setVisibility(View.VISIBLE);
                Logger.d("get net video error: " + error);
            }
        });
    }
}
