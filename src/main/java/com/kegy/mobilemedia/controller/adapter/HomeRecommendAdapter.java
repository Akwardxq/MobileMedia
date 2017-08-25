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
import com.kegy.mobilemedia.model.media.NetVideo;
import com.kegy.mobilemedia.utils.Logger;

import java.util.List;

/**
 * Created by Administrator on 2017/8/24.
 */

public class HomeRecommendAdapter extends BaseAdapter {

    private NetVideo mNetVideo;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public HomeRecommendAdapter(NetVideo netVideo, Context context) {
        this.mNetVideo = netVideo;
        this.mContext = context.getApplicationContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mNetVideo.trailers.size();
    }

    @Override
    public Object getItem(int position) {
        return mNetVideo.trailers.get(position);
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
        NetVideo.VideoItem videoItem = (NetVideo.VideoItem) getItem(position);
        viewHolder.name.setText(videoItem.movieName);
        viewHolder.desc.setText(TextUtils.isEmpty(videoItem.videoTitle)?"暂无":videoItem.videoTitle);
        Glide.with(mContext)
                .load(videoItem.coverImg)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(viewHolder.header);
        return convertView;
    }

    class ViewHolder {
        public ImageView header;
        public TextView name, desc;
    }
}
