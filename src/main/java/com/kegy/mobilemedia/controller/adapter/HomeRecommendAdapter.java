package com.kegy.mobilemedia.controller.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.model.account.RankListResponse;
import com.kegy.mobilemedia.utils.Logger;

import java.util.List;

/**
 * Created by Administrator on 2017/8/24.
 */

public class HomeRecommendAdapter extends BaseAdapter {


    private List<RankListResponse.RankListItem> mRankListItems;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public HomeRecommendAdapter(List<RankListResponse.RankListItem> items, Context context) {
        Logger.d("NetVideoAdapter constructor items size: " + items.size());
        this.mRankListItems = items;
        this.mContext = context.getApplicationContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mRankListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mRankListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Logger.d("getView: " + position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater
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
        RankListResponse.RankListItem item = (RankListResponse.RankListItem) getItem(position);
        viewHolder.name.setText(item.name);
        viewHolder.desc.setText(TextUtils.isEmpty(item.desc)?"暂无":item.desc);
        Glide.with(mContext)
                .load(item.poster_list.getPostUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(viewHolder.header);
        return convertView;
    }

    class ViewHolder {
        public ImageView header;
        public TextView name, desc;
    }
}
