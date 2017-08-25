package com.kegy.mobilemedia.controller.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.model.media.MediaItem;
import com.kegy.mobilemedia.utils.common.SizeUtils;
import com.kegy.mobilemedia.utils.common.TimeUtils;

import java.util.List;

/**
 * Created by kegy on 2017/8/9.
 */

public class LocalAudioAdapter extends BaseAdapter {

    private List<MediaItem> mMediaItems;
    private LayoutInflater mLayoutInflater;

    public LocalAudioAdapter(List<MediaItem> items, Context context) {
        this.mMediaItems = items;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mMediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.local_audio_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.thumbnail = (ImageView) convertView.
                    findViewById(R.id.iv_local_video_thumbnail);
            viewHolder.displayName = (TextView) convertView.
                    findViewById(R.id.tv_local_video_display_name);
            viewHolder.duration = (TextView) convertView.
                    findViewById(R.id.tv_local_video_duration);
            viewHolder.size = (TextView) convertView.
                    findViewById(R.id.tv_local_video_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MediaItem mediaItem = (MediaItem) getItem(position);
        viewHolder.displayName.setText(mediaItem.getDisplayName());
        TextPaint paint = viewHolder.displayName.getPaint();
        paint.setFakeBoldText(true);
        viewHolder.duration.setText(TimeUtils.toTimeStr(mediaItem.getDuration()));
        viewHolder.size.setText(SizeUtils.toSizeStr(mediaItem.getSize()));
        return convertView;
    }

    class ViewHolder {
        public ImageView thumbnail;
        public TextView displayName, duration, size;
    }
}
