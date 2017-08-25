package com.kegy.mobilemedia.controller.fragment.page2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kegy.mobilemedia.MediaApp;
import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.activity.SystemVideoPlayerActivity;
import com.kegy.mobilemedia.controller.base.BaseFragment;
import com.kegy.mobilemedia.model.account.SeriesInfoList;
import com.kegy.mobilemedia.model.account.TypeList;
import com.kegy.mobilemedia.model.media.SerializableList;
import com.kegy.mobilemedia.model.program.ProgramList;
import com.kegy.mobilemedia.model.program.ProgramList.*;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.Logger;
import com.kegy.mobilemedia.utils.Toaster;
import com.kegy.mobilemedia.utils.http.HttpUtils;
import com.kegy.mobilemedia.utils.manager.APIManager;
import com.kegy.mobilemedia.utils.manager.MobileDataManager;

import java.util.Collections;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Administrator on 2017/8/24.
 */

public class SortedMediaPageFragment extends Fragment {

    private static final String EXTRA_TYPE_ID = "type_id";
    private int mTypeId;
    private TypeList.TypeChildren mTypeChildren;
    private List<ProgramList.ProgramListItem> mProgramListItems;
    private SwipeRefreshLayout mRefreshLayout;
    private ListView mListView;
    private RecommendAdapter mAdapter;
    private TextView mTextView;

    public static SortedMediaPageFragment newInstance(Integer integer) {
        SortedMediaPageFragment fragment = new SortedMediaPageFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TYPE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(bindContentView(),container,false);
        initView(view);
        getData();
        return view;
    }

    protected int bindContentView() {
        return R.layout.fragment_sort_media_page;
    }

    protected void initView(View view) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sp_recommend_page);
        mListView = (ListView) view.findViewById(R.id.lv_home_page_recommend);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProgramListItem item = (ProgramListItem) mAdapter.getItem(position);
                Logger.d("item id: " + item.getId());
                getMovieSeriesList(item.getSeries_id());
            }
        });
        mTextView = (TextView) view.findViewById(R.id.tv_fragment_sort_media_nodata);
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

    protected void getData() {
        mTypeId = (int) getArguments().getSerializable(EXTRA_TYPE_ID);
        mTypeChildren = MediaApp.getTypeChildrenById(mTypeId);
        addModule();
    }

    private void addModule() {
        if (mTypeChildren != null && mTypeChildren.getChildren() != null) {
            mTextView.setVisibility(View.GONE);
            int size = mTypeChildren.getChildren().size();
            Logger.d("addMovieOrSeriesModule size: " + size);
            for (int i = 0; i < size; i++) {
                TypeList.TypeChildren temp = mTypeChildren.getChildren().get(i);
                getProgramContent(temp.getId(), 80);
            }
        } else {
            Logger.d("addMovieOrSeriesModule type children is null");
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    private void getProgramContent(final int label, int row) {
        APIManager.getProgramList(label + "", 1, row, "0", null, new HttpUtils.StringResponseListener() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    ProgramList response = MobileDataManager.getGson().
                            fromJson(result, ProgramList.class);
                    if (response.getList() != null) {
                        int size = response.getList().size();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.setRefreshing(false);
                            }
                        });
                        if (size > 0) {
                            Logger.d("getProgramContent size: " + size);
                            showData(response.getList());
                        } else {
                            onRequestFailed();
                        }
                    }
                } else {
                    onRequestFailed();
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void onRequestFailed() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showData(final List<ProgramList.ProgramListItem> programListItems) {
        if (mTypeChildren == null) {
            Logger.d("TypeChildren is null");
            return;
        }
        this.mProgramListItems = programListItems;
        if (mAdapter == null) {
            Logger.d("set Adapter");
            mListView.setAdapter(mAdapter = new RecommendAdapter(getActivity(), mProgramListItems));
        } else {
            Logger.d("notify data change");
            mAdapter.mProgramListItems = programListItems;
            mAdapter.notifyDataSetChanged();
        }
    }

    private class RecommendAdapter extends BaseAdapter {

        public List<ProgramListItem> mProgramListItems;
        private LayoutInflater mInflater;

        public RecommendAdapter(Context ctx, List<ProgramListItem> programListItems) {
            this.mProgramListItems = programListItems;
            mInflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return mProgramListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mProgramListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater
                        .inflate(R.layout.net_video_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.header = (ImageView)
                        convertView.findViewById(R.id.net_video_img);
                viewHolder.name = (TextView)
                        convertView.findViewById(R.id.net_video_name);
                viewHolder.desc = (TextView)
                        convertView.findViewById(R.id.net_video_desc);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ProgramListItem item = (ProgramListItem) getItem(position);
            String posterUrl = item.getPoster_list().getPostUrl(Config.NORMAL_POSTER_SIZE);
            Logger.d("poster url: " + posterUrl);
            Glide.with(getActivity())
                    .load(posterUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(viewHolder.header);
            viewHolder.name.setText(item.getName());
            viewHolder.desc.setText(item.getDesc());
            return convertView;
        }

        private class ViewHolder {
            public ImageView header;
            public TextView name, desc;
        }
    }

}
