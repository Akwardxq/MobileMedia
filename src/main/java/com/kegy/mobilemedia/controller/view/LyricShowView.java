package com.kegy.mobilemedia.controller.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;


import com.kegy.mobilemedia.model.media.Lyric;

import java.util.List;

/**
 * Created by kegy on 2017/8/14.
 */

public class LyricShowView extends TextView {
    private static final String TAG = "LyricShowView";

    //歌词列表
    private List<Lyric> mLyrics;
    private int mCurrentIndex = 30;
    private int mPerHeight = 60;
    private Lyric mCurrentLyric;

    public void setLyrics(List<Lyric> lyrics) {
        mLyrics = lyrics;
    }

    private Paint mNormalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPlayingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LyricShowView(Context context) {
        this(context, null);
    }

    public LyricShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mNormalPaint.setColor(Color.WHITE);
        mNormalPaint.setTextSize(50);
        mNormalPaint.setTextAlign(Paint.Align.CENTER);
        mPlayingPaint.setColor(Color.GREEN);
        mPlayingPaint.setTextSize(50);
        mPlayingPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isLyricsValid()) {
            String content = mLyrics.get(mCurrentIndex).getContent();
            canvas.drawText(content, getWidth() / 2, getHeight() / 2, mPlayingPaint);

            int tmpY = getHeight() / 2;
            for (int i = mCurrentIndex - 1; i >= 0; i--) {
                String preContent = mLyrics.get(i).getContent();
                tmpY -= mPerHeight;
                if (tmpY < 0) {
                    break;
                }
                canvas.drawText(preContent, getWidth() / 2, tmpY, mNormalPaint);
            }
            tmpY = getHeight() / 2;
            for (int j = mCurrentIndex + 1; j < mLyrics.size(); j++) {
                String preContent = mLyrics.get(j).getContent();
                tmpY += mPerHeight;
                if (tmpY > getHeight()) {
                    break;
                }
                canvas.drawText(preContent, getWidth() / 2, tmpY, mNormalPaint);
            }

        } else {
            canvas.drawText("没有歌词文件", getWidth() / 2, getHeight() / 2, mPlayingPaint);
        }
    }

    /**
     * 设置当前要显示的歌词
     *
     * @param position
     */
    public void setShowLyric(int position) {
        if (isLyricsValid()) {
            for (int i = 1; i < mLyrics.size(); i++) {
                if (mLyrics.get(i).getTimePoint() >= position) {
                    int tempIndex = i -1;
                    if (mLyrics.get(tempIndex).getTimePoint() <= position) {
                        this.mCurrentIndex = tempIndex;
                        this.mCurrentLyric = mLyrics.get(tempIndex);
                        break;
                    }
                }
            }
        }
        invalidate();
    }

    public boolean isLyricsValid() {
        return mLyrics != null && mLyrics.size() > 0;
    }
}
