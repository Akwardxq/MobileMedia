package com.kegy.mobilemedia.model.media;

import java.io.Serializable;

/**
 * Created by kegy on 2017/8/9.
 */

public class MediaItem implements Serializable {

    private String mDisplayName;
    private long mDuration;
    private long mSize;
    private String mPath;
    private String mArtist;

    public MediaItem(){
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

}
