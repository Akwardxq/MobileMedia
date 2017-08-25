package com.kegy.mobilemedia.model.account;

/**
 * Created by Administrator on 2017/8/18.
 */

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kegy.mobilemedia.controller.base.BaseResponse;
import com.kegy.mobilemedia.model.program.PosterList;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.common.DomainUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoDetail extends BaseResponse {

    @Expose
    private String actors;
    @Expose
    private String country;
    @Expose
    private int definition;
    @Expose
    private long degrade_num;
    @Expose
    @SerializedName("demand_url")
    private List<String> demand_url;
    @Expose
    private String desc;
    @Expose
    private String director;
    @Expose
    private int duration;
    //	@Expose
//	private String format;
    @Expose
    private String iframe_url;
    @Expose
    private String iframe_dir;
    @Expose
    private int is_favourite;
    @Expose
    private int is_free;
    @Expose
    private int is_purchased;
    @Expose
    private String label;
    @Expose
    private String label_name;
    //	@Expose
//	private String languages;
    @Expose
    private String last_id;
    @Expose
    private int my_score;
    @Expose
    private String next_id;
    @Expose
    private long off_time;
    @Expose
    private int play_time;
    @Expose
    private String play_token;
    @Expose
    private PosterList poster_list;
    @Expose
    private long praise_num;
    @Expose
    private int price;
    @Expose
    private int score;
    @Expose
    private int score_num;
    @Expose
    private String screen_time;
    @Expose
    private String series_id;
    @Expose
    private Integer series_total;
    @Expose
    private Integer content_type;
    @Expose
    private String size;
    @Expose
    private int times;
    @Expose
    private String video_idx;
    @Expose
    private String video_name;
    @Expose
    private int video_num;
    @Expose
    private int volume_compensation;
    @Expose
    private int status;
    @Expose
    @SerializedName("mark_info")
    private String mark_info;
    @Expose
    @SerializedName("rate_list")
    private ArrayList<String> rate_list;

    public String tab;

    @Expose
    private int my_praise_record;
    @Expose
    private int my_score_record;

    @Expose
    private int ad_needed;//终端是否需要去获取视频贴片广告，取值1：不需要，0：需要

    @Expose
    @SerializedName("abstract")
    private String abstract_Introduction;

    //数值型，视频来源，取值0：上传的视频（默认）1：拆条节目 2回看转点播节目 3：第三方无内容媒资
    private int video_source;

    /**
     * video_source（1或2）需要加回看图标
     * */
    public int getVideo_source() {
        return video_source;
    }

    public void setVideo_source(int video_source) {
        this.video_source = video_source;
    }

    public String getAbstract_Introduction() {
        return abstract_Introduction;
    }

    public void setAbstract_Introduction(String abstract_Introduction) {
        this.abstract_Introduction = abstract_Introduction;
    }

    public int getAd_needed() {
        return ad_needed;
    }

    public void setAd_needed(int ad_needed) {
        this.ad_needed = ad_needed;
    }

    public int getMy_praise_record() {
        return my_praise_record;
    }

    public void setMy_praise_record(int my_praise_record) {
        this.my_praise_record = my_praise_record;
    }

    public int getMy_score_record() {
        return my_score_record;
    }

    public void setMy_score_record(int my_score_record) {
        this.my_score_record = my_score_record;
    }

    @Expose
    private int free_trial_time;

    public int getFree_trial_time() {
        return free_trial_time;
    }

    public void setFree_trial_time(int free_trial_time) {
        this.free_trial_time = free_trial_time;
    }

    public ArrayList<String> getRate_list() {
        return rate_list;
    }

    public void setRate_list(ArrayList<String> rate_list) {
        this.rate_list = rate_list;
    }
    public String getMark_info() {
        return mark_info;
    }

    public void setMark_info(String mark_info) {
        this.mark_info = mark_info;
    }
    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDefinition() {
        return definition;
    }

    public void setDefinition(int definition) {
        this.definition = definition;
    }

    public String getShowDegrade_num()
    {
        String result=""+degrade_num;
        if(praise_num>=10000)
            result=""+1.0*degrade_num/10000+"万";
        return result;
    }
    public long getDegrade_num() {
        return degrade_num;
    }

    public void setDegrade_num(long degrade_num) {
        this.degrade_num = degrade_num;
    }

    public List<String> getDemand_url() {
        return demand_url;
    }

    public void setDemand_url(List<String> demand_url) {
        this.demand_url = demand_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public String getIframe_url() {
        //替换域名
        if(!TextUtils.isEmpty(Config.SERVER_IP)){
            return DomainUtils.replaceDomain(iframe_url);
        }
        return iframe_url;
    }

    public void setIframe_url(String iframe_url) {
        this.iframe_url = iframe_url;
    }

    public int getIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(int is_favourite) {
        this.is_favourite = is_favourite;
    }

    public int getIs_free() {
        return is_free;
    }

    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }

    public int getIs_purchased() {
        return is_purchased;
    }

    public void setIs_purchased(int is_purchased) {
        this.is_purchased = is_purchased;
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

    public String getLast_id() {
        return last_id;
    }

    public void setLast_id(String last_id) {
        this.last_id = last_id;
    }

    public int getMy_score() {
        return my_score;
    }

    public void setMy_score(int my_score) {
        this.my_score = my_score;
    }

    public String getNext_id() {
        return next_id;
    }

    public void setNext_id(String next_id) {
        this.next_id = next_id;
    }

    public long getOff_time() {
        return off_time;
    }

    public void setOff_time(long off_time) {
        this.off_time = off_time;
    }

    public int getPlay_time() {
        return play_time;
    }

    public void setPlay_time(int play_time) {
        this.play_time = play_time;
    }

    public String getPlay_token() {
        return play_token;
    }

    public void setPlay_token(String play_token) {
        this.play_token = play_token;
    }

    public PosterList getPoster_list() {
        return poster_list;
    }

    public void setPoster_list(PosterList poster_list) {
        this.poster_list = poster_list;
    }

    public String getShowPraise_num()
    {
        String result=""+praise_num;
        if(praise_num>=10000)
            result=""+1.0*praise_num/10000+"万";
        return result;
    }
    public long getPraise_num() {
        return praise_num;
    }

    public void setPraise_num(long praise_num) {
        this.praise_num = praise_num;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore_num() {
        return score_num;
    }

    public void setScore_num(int score_num) {
        this.score_num = score_num;
    }

    public String getScreen_time() {
        return screen_time;
    }

    public void setScreen_time(String screen_time) {
        this.screen_time = screen_time;
    }

    public String getSeries_id() {
        return series_id;
    }

    public void setSeries_id(String series_id) {
        this.series_id = series_id;
    }

    public Integer getContent_type() {
        return content_type;
    }

    public void setContent_type(Integer content_type) {
        this.content_type = content_type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getSeries_total() {
        return series_total;
    }

    public void setSeries_total(Integer series_total) {
        this.series_total = series_total;
    }

    public String getShowTimes()
    {
        String result=""+times;
        if(times>10000){
//			result=""+1.0*times/10000+"万";
            result=String.format("%.2f", 1.0*times/10000)+"万";
        }
        return result;
    }
    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getVideo_idx() {
        return video_idx;
    }

    public void setVideo_idx(String video_idx) {
        this.video_idx = video_idx;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public int getVideo_num() {
        return video_num;
    }

    public void setVideo_num(int video_num) {
        this.video_num = video_num;
    }

    public int getVolume_compensation() {
        return volume_compensation;
    }

    public void setVolume_compensation(int volume_compensation) {
        this.volume_compensation = volume_compensation;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIframe_dir() {
        return iframe_dir;
    }

    public void setIframe_dir(String iframe_dir) {
        this.iframe_dir = iframe_dir;
    }

    /**
     * 获取demand_url中地址
     * */
    public String getDemandUrlByPosition(int position){
        if(demand_url!=null&&demand_url.size()>0){
            String url;
            if(position<demand_url.size()){
                url=demand_url.get(position);
            }else{
                url=demand_url.get(0);
            }
            if(!TextUtils.isEmpty(Config.SERVER_IP)){
                url=DomainUtils.replaceDomain(url);
            }
            return url;
        }
        return null;
    }

}
