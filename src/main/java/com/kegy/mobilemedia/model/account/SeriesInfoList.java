package com.kegy.mobilemedia.model.account;


import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kegy.mobilemedia.controller.base.BaseResponse;
import com.kegy.mobilemedia.model.program.PosterList;

import java.io.Serializable;
import java.util.List;

public class SeriesInfoList extends BaseResponse {

    @Expose
    private String series_name;
    @Expose
    private int series_num;
    @Expose
    @SerializedName("series_poster_list")
    private PosterList series_poster_list;
    @Expose
    private String actors;
    @Expose
    private String director;
    @Expose
    private String country;
    @Expose
    private String languages;
    @Expose
    private String label;
    @Expose
    private String label_name;
    @Expose
    private String series_desc;
    @Expose
    private long times;
    @Expose
    private String last_viewed_idx;
    @Expose
    @SerializedName("video_poster_list")
    private PosterList video_poster_list;
    @Expose
    private String providerid;
    @Expose
    private int is_favourite;
    @Expose
    public int content_type;


    public int getIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(int is_favourite) {
        this.is_favourite = is_favourite;
    }

    public String getProviderid() {
        return providerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
    }

    public String getShowTimes() {
        String result = "" + times;
        if (times > 10000) {
//			result=""+1.0*times/10000+"万";
            result = String.format("%.2f", 1.0 * times / 10000) + "万";
        }
        return result;
    }

    public long getTimes() {
        return times;
    }


    public void setTimes(long times) {
        this.times = times;
    }


    @Expose
    @SerializedName("video_list")
    private List<SeriesInfoListItem> video_list;


    public String getSeries_name() {
        return series_name;
    }


    public void setSeries_name(String series_name) {
        this.series_name = series_name;
    }


    public int getSeries_num() {
        return series_num;
    }


    public void setSeries_num(int series_num) {
        this.series_num = series_num;
    }


    public PosterList getSeries_poster_list() {
        return series_poster_list;
    }


    public void setSeries_poster_list(PosterList series_poster_list) {
        this.series_poster_list = series_poster_list;
    }


    public PosterList getVideo_poster_list() {
        return video_poster_list;
    }

    public void setVideo_poster_list(PosterList video_poster_list) {
        this.video_poster_list = video_poster_list;
    }

    public String getPostUrl(String id) {
        return video_poster_list.getPostUrl(id);
//		if(!TextUtils.isEmpty(id)&&video_poster_list.getDir().endsWith("poster/movie/"))
//		{
//			String dir = video_poster_list.getDir();
//			video_poster_list.setDir(dir+id+"/");
//			String posturl = video_poster_list.getPostUrl();
//			video_poster_list.setDir(dir);
//			return posturl;
//		}else return video_poster_list.getPostUrl();
    }

    public String getActors() {
        return actors;
    }


    public void setActors(String actors) {
        this.actors = actors;
    }


    public String getDirector() {
        return director;
    }


    public void setDirector(String director) {
        this.director = director;
    }


    public String getCountry() {
        return country;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public String getLanguages() {
        return languages;
    }


    public void setLanguages(String languages) {
        this.languages = languages;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getLabel_name() {
        return label_name;
    }


    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }


    public String getSeries_desc() {
        return series_desc;
    }


    public void setSeries_desc(String series_desc) {
        this.series_desc = series_desc;
    }


    public List<SeriesInfoListItem> getVideo_list() {
        return video_list;
    }

    public void setVideo_list(List<SeriesInfoListItem> video_list) {
        this.video_list = video_list;
    }

    public String getLast_viewed_idx() {
        return last_viewed_idx;
    }

    public void setLast_viewed_idx(String last_viewed_idx) {
        this.last_viewed_idx = last_viewed_idx;
    }


    public static class SeriesInfoListItem implements Serializable {
        @Expose
        private String video_id;
        @Expose
        private String video_name;
        @Expose
        private String series_idx;
        @Expose
        @SerializedName("video_url")
        private List<String> video_url;
        //		@Expose
//		private String format;
        @Expose
        @SerializedName("video_poster_list")
        private PosterList video_poster_list;
        @Expose
        private int duration;
        //		@Expose
//		private String size;
        @Expose
        private int definition;
        //		@Expose
//		private int transcode_flag;
        @Expose
        private int is_view;
        @Expose
        private long last_viewed_time;
        @Expose
        private long update_time;
        //		@Expose
//		private int off_time;
        @Expose
        private int volume_compensation;
        @Expose
        private String video_desc;

        //在新版搜索需要用到这个参数
        @Expose
        @SerializedName("rate_list")
        private List<String> rate_list;

        @Expose
        @SerializedName("abstract")
        private String abstract_Introduction;

        public String getAbstract_Introduction() {
            return abstract_Introduction;
        }

        public void setAbstract_Introduction(String abstract_Introduction) {
            this.abstract_Introduction = abstract_Introduction;
        }

        //		@Expose
//		@SerializedName("mark_info")
//		private String mark_info;
        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public String getVideo_name() {
            return video_name;
        }

        public void setVideo_name(String video_name) {
            this.video_name = video_name;
        }

        public long getLast_viewed_time() {
            return last_viewed_time;
        }

        public void setLast_viewed_time(long last_viewed_time) {
            this.last_viewed_time = last_viewed_time;
        }

        public long getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(long update_time) {
            this.update_time = update_time;
        }

        public String getPlayShowEvent_idx() {
            String result = "";
            if (TextUtils.isDigitsOnly(series_idx)) {
                if (series_idx.length() < 8)
                    result = series_idx;
                else if (series_idx.length() == 8) {
                    //substring为前闭后开的半闭区间
                    result = series_idx.substring(4, 6) + "-" + series_idx.substring(6);
                } else {
                    long second = Long.parseLong(series_idx);
//                    result = TimeHelper.getDate_f(second);
                }
            } else
                result = series_idx;
            return result;
        }

        public String getShowEvent_idx() {
            String result = "";
            if (TextUtils.isDigitsOnly(series_idx)) {
                if (series_idx.length() < 8)
                    result = series_idx;
                else if (series_idx.length() == 8) {
                    //substring为前闭后开的半闭区间
                    result = series_idx.substring(0, 4) + "-" + series_idx.substring(4, 6) + "-" + series_idx.substring(6);

                } else {
                    long second = Long.parseLong(series_idx);
//                    result = TimeHelper.getDateString_a(second);
                }
            } else
                result = series_idx;
            return result;
        }

        public String getSeries_idx() {
            return series_idx;
        }

        public void setSeries_idx(String series_idx) {
            this.series_idx = series_idx;
        }

        public List<String> getVideo_url() {
            return video_url;
        }

        public void setVideo_url(List<String> video_url) {
            this.video_url = video_url;
        }

        //		public String getFormat() {
//			return format;
//		}
//		public void setFormat(String format) {
//			this.format = format;
//		}
        public PosterList getVideo_poster_list() {
            return video_poster_list;
        }

        public void setVideo_poster_list(PosterList video_poster_list) {
            this.video_poster_list = video_poster_list;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        //		public String getSize() {
//			return size;
//		}
//		public void setSize(String size) {
//			this.size = size;
//		}
        public int getDefinition() {
            return definition;
        }

        public void setDefinition(int definition) {
            this.definition = definition;
        }

        //		public int getTranscode_flag() {
//			return transcode_flag;
//		}
//		public void setTranscode_flag(int transcode_flag) {
//			this.transcode_flag = transcode_flag;
//		}
//		public int getIs_view() {
//			return is_view;
//		}
//		public void setIs_view(int is_view) {
//			this.is_view = is_view;
//		}
//		public int getOff_time() {
//			return off_time;
//		}
//		public void setOff_time(int off_time) {
//			this.off_time = off_time;
//		}
        public int getVolume_compensation() {
            return volume_compensation;
        }

        public void setVolume_compensation(int volume_compensation) {
            this.volume_compensation = volume_compensation;
        }

        public String getVideo_desc() {
            return video_desc;
        }

        public void setVideo_desc(String video_desc) {
            this.video_desc = video_desc;
        }

        //		public String getMark_info() {
//			return mark_info;
//		}
//		public void setMark_info(String mark_info) {
//			this.mark_info = mark_info;
//		}
        public List<String> getRate_list() {
            return rate_list;
        }

        public void setRate_list(List<String> rate_list) {
            this.rate_list = rate_list;
        }

        public int getIs_view() {
            return is_view;
        }

        public void setIs_view(int is_view) {
            this.is_view = is_view;
        }


    }
}
