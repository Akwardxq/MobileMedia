package com.kegy.mobilemedia.controller.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by kegy on 2017/8/10.
 */

public class VideoView extends android.widget.VideoView {

    public VideoView(Context context) {
        this(context, null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置视频的宽和高
     * @param width
     * @param height
     */
    public void setVideoSize(int width,int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

}
