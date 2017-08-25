package com.kegy.mobilemedia.model.media;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/25.
 */

public class NetVideo {

    public List<VideoItem> trailers = new ArrayList<>();

    public class VideoItem {
        public String coverImg;
        public String hightUrl;
        public String id;
        public String movieId;
        public String movieName;
        public String rating;
        public String summary;
        public String url;
        public String videoLength;
        public String videoTitle;
    }

}