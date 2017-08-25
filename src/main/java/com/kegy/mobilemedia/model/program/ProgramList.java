package com.kegy.mobilemedia.model.program;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kegy.mobilemedia.controller.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

public class ProgramList extends BaseResponse {

    @Expose
    @SerializedName("list")
    private List<ProgramListItem> list;
    @Expose
    private int total;

    public List<ProgramListItem> getList() {
        return list;
    }


    public void setList(List<ProgramListItem> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }


    public void setTotal(int total) {
        this.total = total;
    }


    public static class  ProgramListItem implements Serializable{

        /**
         * 手机直播-22
         */
        private int type;
        @Expose
        private String id;
        @Expose
        private String name;
        @Expose
        private int definition;
        @Expose
        private int score;
        @Expose
        private long times;
        @Expose
        private long praise_num;
        @Expose
        private long degrade_num;
        @Expose
        @SerializedName("url")
        private List<String> url;
        @Expose
        private PosterList poster_list;
        @Expose
        private String series_id;
        @Expose
        private String series_idx;
        @Expose
        private int series_total;
        @Expose
        private String start_time;
        @Expose
        private String end_time;
        @Expose
        private long duration;
        @Expose
        private String singer_name;
        @Expose
        private String album_name;
        @Expose
        private String actors;
        @Expose
        private String director;
        @Expose
        private String country;
        @Expose
        private String language;
        @Expose
        private String desc;
        @Expose
        private int is_purchased;
        @Expose
        private int is_hide;
        @Expose
        private int is_favorite;
        @Expose
        private long price;
        @Expose
        private long currency;
        @Expose
        private String copyright;
        @Expose
        private String providerid;
        @Expose
        private int channel_num;
        @Expose
        private String current_idx;
        @Expose
        @SerializedName("popular_program")
        private List<PopularProgram> popular_program;

        @Expose
        @SerializedName("attribute_info")
        private List<AttributeInfo> attribute_info;
        @Expose
        private String release_time;
        @Expose
        private String source;
        @Expose
        private String matching_word;
        @Expose
        private String matching_idx;
        @Expose
        private String lyricist;
        @Expose
        private String composer;
        @Expose
        private int is_ktv;
        @Expose
        private String tag;
        @Expose
        private int comment_num;

        @Expose
        private int content_type;

        @Expose
        private int link_type;

        /**
         * 判断是否加回看角标 providerid==playback
         * */
        public boolean isAddLookbackCorner(){
            if(!TextUtils.isEmpty(providerid)&&providerid.equals("playback")){
                return true;
            }
            return false;
        }

        public int getLink_type() {
            return link_type;
        }
        public void setLink_type(int link_type) {
            this.link_type = link_type;
        }

        @Expose
        @SerializedName("abstract")
        private String abstract_Introduction;

        /**
         * 手机直播新增--直播间状态，0：直播关闭，1：直播中，2:直播冻结
         * */
        public int status;

        /**
         * 手机直播新增--主播id
         * */
        public long creater_id;

        /**
         * 手机直播新增--房间在线人数
         * */
        public long online_num;

        /**
         * 手机直播新增--媒资内容子类型，取值见媒资类型定义标准。
         * */
        public String sub_type;

        public int getContent_type() {
            return content_type;
        }
        public void setContent_type(int content_type) {
            this.content_type = content_type;
        }
        public String getShowEvent_idx()
        {
            String result="";
            if(series_idx!=null&&TextUtils.isDigitsOnly(series_idx))
            {
                if(series_idx.length()<8)
                    result = series_idx+"集";
                else if(series_idx.length()==8)
                {
                    result=series_idx;
                }else
                {
                    long second=Long.parseLong(series_idx);
                }
            }
            else
                result = series_idx;
            return result;
        }
        public String getShowCurrent_idx()
        {
            String result="";
            if(TextUtils.isEmpty(current_idx)||current_idx.equals("0"))  //与ios同步处理，current_idx为0时取series_idx
                current_idx = series_idx;
            if(current_idx!=null&&TextUtils.isDigitsOnly(current_idx))
            {
                if(current_idx.length()<8)
                    result = current_idx+"集";
                else if(current_idx.length()==8)
                {
                    result=current_idx+"期";
                }else
                {
                    long second=Long.parseLong(current_idx);
                }
            }
            else
                result = current_idx;
            return result;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getRelease_time() {
            return release_time;
        }

        public void setRelease_time(String release_time) {
            this.release_time = release_time;
        }

        public int getComment_num() {
            return comment_num;
        }

        public void setComment_num(int comment_num) {
            this.comment_num = comment_num;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDefinition() {
            return definition;
        }

        public void setDefinition(int definition) {
            this.definition = definition;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
        public String getShowTimes()
        {
            String result=""+times;
            if(times>10000){
//				result=""+1.0*times/10000+"万";
                result=String.format("%.2f", 1.0*times/10000)+"万";
            }
            return result;
        }
        public long getTimes() {
            return times;
        }

        public void setTimes(long times) {
            this.times = times;
        }

        public List<String> getUrl() {
            return url;
        }

        public void setUrl(List<String> url) {
            this.url = url;
        }

        public PosterList getPoster_list() {
            return poster_list;
        }

        public void setPoster_list(PosterList poster_list) {
            this.poster_list = poster_list;
        }

        public String getSeries_idx() {
            return series_idx;
        }

        public void setSeries_idx(String series_idx) {
            this.series_idx = series_idx;
        }

        public int getSeries_total() {
            return series_total;
        }

        public void setSeries_total(int series_total) {
            this.series_total = series_total;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
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

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getSeries_id() {
            return series_id;
        }

        public void setSeries_id(String series_id) {
            this.series_id = series_id;
        }

        public List<PopularProgram> getPopular_program() {
            return popular_program;
        }

        public void setPopular_program(List<PopularProgram> popular_program) {
            this.popular_program = popular_program;
        }

        public String getSinger_name() {
            return singer_name;
        }

        public void setSinger_name(String singer_name) {
            this.singer_name = singer_name;
        }

        public String getAlbum_name() {
            return album_name;
        }

        public void setAlbum_name(String album_name) {
            this.album_name = album_name;
        }

        public List<AttributeInfo> getAttribute_info() {
            return attribute_info;
        }

        public void setAttribute_info(List<AttributeInfo> attribute_info) {
            this.attribute_info = attribute_info;
        }

        public String getLyricist() {
            return lyricist;
        }

        public void setLyricist(String lyricist) {
            this.lyricist = lyricist;
        }

        public String getComposer() {
            return composer;
        }

        public void setComposer(String composer) {
            this.composer = composer;
        }

        public int getIs_ktv() {
            return is_ktv;
        }

        public void setIs_ktv(int is_ktv) {
            this.is_ktv = is_ktv;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getMatching_word() {
            return matching_word;
        }

        public void setMatching_word(String matching_word) {
            this.matching_word = matching_word;
        }

        public long getPraise_num() {
            return praise_num;
        }

        public void setPraise_num(long praise_num) {
            this.praise_num = praise_num;
        }

        public long getDegrade_num() {
            return degrade_num;
        }

        public void setDegrade_num(long degrade_num) {
            this.degrade_num = degrade_num;
        }

        public int getIs_purchased() {
            return is_purchased;
        }

        public void setIs_purchased(int is_purchased) {
            this.is_purchased = is_purchased;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public long getCurrency() {
            return currency;
        }

        public void setCurrency(long currency) {
            this.currency = currency;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public String getProviderid() {
            return providerid;
        }

        public void setProviderid(String providerid) {
            this.providerid = providerid;
        }

        public int getChannel_num() {
            return channel_num;
        }

        public void setChannel_num(int channel_num) {
            this.channel_num = channel_num;
        }

        public String getCurrent_idx() {
            return current_idx;
        }

        public void setCurrent_idx(String current_idx) {
            this.current_idx = current_idx;
        }

        public String getMatching_idx() {
            return matching_idx;
        }

        public void setMatching_idx(String matching_idx) {
            this.matching_idx = matching_idx;
        }



        public int getIs_hide() {
            return is_hide;
        }
        public void setIs_hide(int is_hide) {
            this.is_hide = is_hide;
        }
        public int getIs_favorite() {
            return is_favorite;
        }
        public void setIs_favorite(int is_favorite) {
            this.is_favorite = is_favorite;
        }



        public String getAbstract_Introduction() {
            return abstract_Introduction;
        }
        public void setAbstract_Introduction(String abstract_Introduction) {
            this.abstract_Introduction = abstract_Introduction;
        }
    }

    public class PopularProgram implements Serializable{
        @Expose
        private String id;
        @Expose
        private String name;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }


    }

    public class AttributeInfo implements Serializable{

    }
}
