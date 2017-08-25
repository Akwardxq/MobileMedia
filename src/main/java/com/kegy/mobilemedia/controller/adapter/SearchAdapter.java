package com.kegy.mobilemedia.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.model.media.SearchBean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/25.
 */

public class SearchAdapter extends BaseAdapter {

    private Context context;
    private final List<SearchBean.ItemData> mediaItems;

    public SearchAdapter(Context context, List<SearchBean.ItemData> mediaItems){
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder;
        if(convertView ==null){
            convertView = View.inflate(context, R.layout.net_video_item,null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.net_video_img);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.net_video_name);
            viewHoder.tv_desc = (TextView) convertView.findViewById(R.id.net_video_desc);

            convertView.setTag(viewHoder);
        }else{
            viewHoder = (ViewHoder) convertView.getTag();
        }
        SearchBean.ItemData mediaItem = mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getItemTitle());
        viewHoder.tv_desc.setText(mediaItem.getKeywords());
        Glide.with(context).load(mediaItem.getItemImage().getImgUrl1())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHoder.iv_icon);
        return convertView;
    }


    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }

}
