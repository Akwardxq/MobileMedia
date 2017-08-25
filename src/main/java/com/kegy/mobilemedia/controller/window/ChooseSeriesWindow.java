package com.kegy.mobilemedia.controller.window;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.base.BaseVideoPlayerActivity;
import com.kegy.mobilemedia.model.account.SeriesInfoList;
import com.kegy.mobilemedia.utils.Config;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class ChooseSeriesWindow {

    private PopupWindow mWindow;
    private View mContentView;
    private GridView mGridView;
    private List<SeriesInfoList.SeriesInfoListItem> mNetMediaItems;
    private BaseVideoPlayerActivity mVideoPlayerActivity;

    public ChooseSeriesWindow(Context context, List<SeriesInfoList.SeriesInfoListItem> items) {
        mContentView = LayoutInflater.from(context)
                .inflate(R.layout.choose_series_window, null, false);
        this.mNetMediaItems = items;
        mVideoPlayerActivity = (BaseVideoPlayerActivity) context;
        mGridView = (GridView) mContentView.findViewById(R.id.lv_all_videos);
    }

    public void show(View view,String playIndex) {
        mWindow = new PopupWindow(mContentView,950, Config.sDeviceHeight);
        mGridView.setAdapter(new AllVideoAdapter(mNetMediaItems,playIndex));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mVideoPlayerActivity.playTo(position);
                hide();
            }
        });
        mWindow.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    public void hide() {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        }
    }

    private class AllVideoAdapter extends BaseAdapter {

        private List<SeriesInfoList.SeriesInfoListItem> mListItems;
        private String mPlayIndex;

        public AllVideoAdapter(List<SeriesInfoList.SeriesInfoListItem> items, String playIndex) {
            this.mListItems = items;
            this.mPlayIndex = playIndex;
        }

        @Override
        public int getCount() {
            return mNetMediaItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNetMediaItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.select_video_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_video_item_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SeriesInfoList.SeriesInfoListItem item = (SeriesInfoList.SeriesInfoListItem) getItem(position);
            if (item.getSeries_idx().equals(mPlayIndex)) {
                viewHolder.title.setText(item.getSeries_idx());
                viewHolder.title.setTextColor(Color.RED);
            } else {
                viewHolder.title.setText(item.getSeries_idx());
            }
            return convertView;
        }

        class ViewHolder {
            public TextView title;
        }
    }

}
