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
    private SwipeRefreshLayout mRefreshLayout;
    private List<RankListResponse.RankListItem> mRankListItems;
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
                RankListResponse.RankListItem item = (RankListResponse.RankListItem) mAdapter
                        .getItem(position);
                getMovieSeriesList(item.series_id);
            }
        });
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sp_refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    /**
     * 获取网络资源中的电影信息
     */
    private void getMovieSeriesList(String seriesId) {
        if (TextUtils.isEmpty(seriesId)) {
            Toaster.toast(getActivity(), "加载电影失败");
            return;
        }
        APIManager.getSeriesInfo(seriesId, 1, 300, new HttpUtils.StringResponseListener() {
            @Override
            public void onSuccess(String result) {
                Logger.d("getMovieSeriesList result: " + result);
                if (result != null) {
                    SeriesInfoList seriesData = MobileDataManager.getGson().
                            fromJson(result, SeriesInfoList.class);
                    List<SeriesInfoList.SeriesInfoListItem> list = seriesData.getVideo_list();
                    if (list != null
                            && list.size() > 0
                            && list.get(0).getSeries_idx().length() >= 8) {
                        // 期数按照降序排列
                        if (list.size() > 1
                                && Long.parseLong(list.get(0)
                                .getSeries_idx()) < Long
                                .parseLong(list.get(1)
                                        .getSeries_idx())) {
                            Collections.reverse(list);
                        }
                    }
                    Logger.d("series num: " + list.size());
                    SerializableList<SeriesInfoList.SeriesInfoListItem> items = new SerializableList<>();
                    items.setList(list);
                    Intent intent = SystemVideoPlayerActivity.newIntent(getActivity(), items);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    protected void getData() {
        if (MediaApp.sLoginInfo != null) {
            getRecommendData();
        }
    }

    private void getRecommendData() {
        mRequestNum = mRequestNum * mRefreshCount;
        mRefreshCount++;
        APIManager.getRankList("1101", "1", "2", "" + mRequestNum, "1", Config.NORMAL_POSTER_SIZE, "1",
                new HttpUtils.StringResponseListener() {
                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            Logger.d("get Recommend Data success");
                            final RankListResponse rankListResponse = MobileDataManager.getGson()
                                    .fromJson(result, RankListResponse.class);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mRefreshLayout.setRefreshing(false);
                                    if (rankListResponse.ret == 0) {
                                        mRankListItems = rankListResponse.list;
                                        if (mRankListItems.size() == 0) {
                                            mTvNoData.setVisibility(View.VISIBLE);
                                        } else if (mAdapter == null) {
                                            mAdapter = new HomeRecommendAdapter(mRankListItems, getActivity());
                                            mListView.setAdapter(mAdapter);
                                        } else {
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        mTvNoData.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(String error) {
                        Logger.d("onError " + error);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.setRefreshing(false);
                                mTvNoData.setVisibility(View.VISIBLE);
                            }
                        });

                    }

                });
    }
}
