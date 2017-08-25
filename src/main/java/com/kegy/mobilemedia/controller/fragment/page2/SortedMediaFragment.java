package com.kegy.mobilemedia.controller.fragment.page2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.kegy.mobilemedia.MediaApp;
import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.adapter.SortedMediaAdapter;
import com.kegy.mobilemedia.controller.base.BaseFragment;
import com.kegy.mobilemedia.model.media.NetAudio;
import com.kegy.mobilemedia.model.widget.UnderLinePageIndicator;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.Logger;
import com.kegy.mobilemedia.utils.http.HttpUtils;
import com.kegy.mobilemedia.utils.manager.MobileDataManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 网络多媒体分类fragment<br>
 *     by kegy
 */
public class SortedMediaFragment extends BaseFragment {

    private ListView mListView;
    private TextView mTextView;
    private NetAudio mNetAudio;
    private SortedMediaAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected int bindContentView() {
        return R.layout.fragment_sorted_media;
    }

    @Override
    protected void initView() {
        mTextView = (TextView) findViewById(R.id.tv_fragment_sort_media_notype);
        mListView = (ListView) findViewById(R.id.lv_fragment_sort_media);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sr_fragment_sort_media);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected void getData() {
        HttpUtils.callJSONAPI(Config.ALL_SRC, new HttpUtils.StringResponseListener() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    mNetAudio = MobileDataManager.getGson().fromJson(result,NetAudio.class);
                    final List<NetAudio.ListEntity> entities = mNetAudio.getList();
                    Iterator<NetAudio.ListEntity> iterator = entities.iterator();
                    while (iterator.hasNext()) {
                        NetAudio.ListEntity entity = iterator.next();
                        if ("video".equals(entity.getType())) {
                            iterator.remove();
                        }
                    }
                    Logger.d("Net Audio size: " + entities.size());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshLayout.setRefreshing(false);
                            if (mAdapter == null) {
                                mListView.setAdapter(mAdapter = new SortedMediaAdapter(getActivity(), entities));
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
