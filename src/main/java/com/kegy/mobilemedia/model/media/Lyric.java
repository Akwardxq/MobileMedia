package com.kegy.mobilemedia.model.media;

/**
 * Created by kegy on 2017/8/14.
 */
public class Lyric implements Comparable<Lyric> {

    /**
     * 时间戳
     */
    private long mTimePoint;

    /**
     * 内容
     */
    private String mContent;

    /**
     * 显示时间
     */
    private long mSleepTime;

    public Lyric() {
    }

    public Lyric(long timePoint, String content, long sleepTime) {
        mTimePoint = timePoint;
        mContent = content;
        mSleepTime = sleepTime;
    }

    public long getTimePoint() {
        return mTimePoint;
    }

    public void setTimePoint(long timePoint) {
        mTimePoint = timePoint;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public long getSleepTime() {
        return mSleepTime;
    }

    public void setSleepTime(long sleepTime) {
        mSleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "mTimePoint=" + mTimePoint +
                ", mContent='" + mContent + '\'' +
                ", mSleepTime=" + mSleepTime +
                '}';
    }

    @Override
    public int compareTo(Lyric o) {
        if (this.getTimePoint() > o.getTimePoint()) {
            return 1;
        } else if (this.getTimePoint() < o.getTimePoint()) {
            return -1;
        }
        return 0;
    }
}
