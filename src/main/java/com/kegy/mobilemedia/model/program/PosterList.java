package com.kegy.mobilemedia.model.program;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.kegy.mobilemedia.utils.common.TimeUtils;

import java.io.Serializable;

public class PosterList implements Serializable {

    public static String NORMAL_POSTER_SIZE = "246x138";

    public static String TOP_POSTER_SIZE = "640x338";

    @Expose
    private String dir;
    @Expose
    private String icon_font;
    @Expose
    private Object list;
    @Expose
    private String live_dir;
    @Expose
    private Object live_list;


    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getIcon_font() {
        return icon_font;
    }

    public void setIcon_font(String icon_font) {
        this.icon_font = icon_font;
    }

    public Object getList() {
        return list;
    }

    public void setList(Object list) {
        this.list = list;
    }

    public String getLive_dir() {
        return live_dir;
    }

    public void setLive_dir(String live_dir) {
        this.live_dir = live_dir;
    }

    public Object getLive_list() {
        return live_list;
    }

    public void setLive_list(Object live_list) {
        this.live_list = live_list;
    }

    public String getSingerIcon(){
        String adUrl = null;
        if (getDir() != null && getList() != null) {
            String adJson = getList().toString().trim();
            if(adJson.contains("138x138")){
                adUrl = getDir() + "138x138_1.jpg";
            }
        }
        return adUrl;
    }
    //专辑海报地址
    public String getAlbumPoster(){
        String adUrl = null;
        if (getDir() != null && getList() != null) {
            String adJson = getList().toString().trim();
            if(adJson.contains("246x138")){
                adUrl = getDir() + "246x138_1.jpg";
            }
        }
        return adUrl;
    }

    public String getPostUrl(String id)
    {
        String adUrl = null;
        if (getDir() != null && getList() != null) {
			/*
			 * getList={100x126=100x126_1.jpg, 113x138=113x138_1.jpg,
			 * 142x172=142x172_1.jpg}
			 */
            String adJson = getList().toString().trim();
            printLog(  "adJson:" + adJson);
            if (adJson.contains(NORMAL_POSTER_SIZE)) {
                //资讯的海报地址不能这么拼凑
//				adUrl = getDir() + Config.NORMAL_POSTER_SIZE + "_1.jpg";
                String[] urls = adJson.replace("{", "").replace("}", "").split(",");
                for (String url : urls) {
                    if ((url.split("=")[0].trim()).equals(NORMAL_POSTER_SIZE)) {
                        if(getDir().endsWith("poster/movie/")&&!TextUtils.isEmpty(id))
                            adUrl = getDir() +id+"/"+ url.split("=")[1];
                        else adUrl = getDir() + url.split("=")[1];
                        break;
                    }
                }
            } else {
                // 否则默认返回第一个海报
                adJson = getAdUrl(adJson);
                if(getDir().endsWith("poster/movie/")&&!TextUtils.isEmpty(id))
                    adUrl = getDir() +id+"/"+ adJson;
                else adUrl = getDir() + adJson;
                printLog( "getAdByPosterList adUrl=" + adUrl);
            }
        }
        return adUrl;
    }


    /*
     * 外部调用
     */
    public String getPostUrl() {
        String adUrl = null;
        if (getDir() != null && getList() != null) {
			/*
			 * getList={100x126=100x126_1.jpg, 113x138=113x138_1.jpg,
			 * 142x172=142x172_1.jpg}
			 */
            String adJson = getList().toString().trim();
            printLog( "adJson:" + adJson);
            if (adJson.contains(NORMAL_POSTER_SIZE)) {
                //资讯的海报地址不能这么拼凑
//				adUrl = getDir() + Config.NORMAL_POSTER_SIZE + "_1.jpg";
                String[] urls = adJson.replace("{", "").replace("}", "").split(",");
                for (String url : urls) {
                    if ((url.split("=")[0].trim()).equals(NORMAL_POSTER_SIZE)) {
                        adUrl = getDir() + url.split("=")[1];
                        break;
                    }
                }
            } else {
                // 否则默认返回第一个海报
                adUrl = getAdByPosterList(this);
            }
        }
        return adUrl;
    }

    /*
     * 外部调用，获取与传入size一样的海报，前提是服务器存在，否则返回第一个海报
     */
    public String getFitPostUrl(String mSize) {
        String adUrl = null;
        if (getDir() != null && getList() != null) {
			/*
			 * getList={100x126=100x126_1.jpg, 113x138=113x138_1.jpg,
			 * 142x172=142x172_1.jpg}
			 */
            String adJson = getList().toString().trim();
            printLog( "adJson:" + adJson);
            if (adJson.contains(mSize)) {
                //资讯的海报地址不能这么拼凑
//				adUrl = getDir() + Config.NORMAL_POSTER_SIZE + "_1.jpg";
                String[] urls = adJson.replace("{", "").replace("}", "").split(",");
                for (String url : urls) {
                    if ((url.split("=")[0].trim()).equals(mSize)) {
//						adUrl = getDir() + url.split("=")[1];
                        String s = url.split("=")[1];
                        if(!TextUtils.isEmpty(s)&&s.contains("|"))
                            s=s.split("\\|")[0];
                        adUrl = getDir() + s;
                        break;
                    }
                }
            } else {
                // 否则默认返回第一个海报
                adUrl = getAdByPosterList(this);
            }
        }
        return adUrl;
    }

    /*
     * 外部调用
     */
    public String getPostUrlBySize(String postsize) {
        String adUrl = null;
        if (getDir() != null && getList() != null) {
            String adJson = getList().toString().trim();
            printLog( "adJson:" + adJson);
            if (adJson.contains(postsize)) {
                //资讯的海报地址不能这么拼凑
//				adUrl = getDir() + Config.NORMAL_POSTER_SIZE + "_1.jpg";
                String[] urls = adJson.replace("{", "").replace("}", "").split(",");
                for (String url : urls) {
                    if ((url.split("=")[0].trim()).equals(postsize)) {
                        String s = url.split("=")[1];
                        if(!TextUtils.isEmpty(s)&&s.contains("|"))
                            s=s.split("\\|")[0];
//						adUrl = getDir() + url.split("=")[1];
                        adUrl = getDir() + s;
                        break;
                    }
                }
            } else {
                // 否则默认返回第一个海报
                adUrl = getAdByPosterList(this);
            }
        }
        return adUrl;
    }

    public String getRealtimePostUrl() {
        String adUrl = null;
        if (getLive_dir() != null && getLive_list() != null) {
			/*
			 * getList={100x126=100x126_1.jpg, 113x138=113x138_1.jpg,
			 * 142x172=142x172_1.jpg}
			 */
            String adJson = getLive_list().toString().trim();
            if (adJson.contains(NORMAL_POSTER_SIZE)) {
                String[] urls = adJson.replace("{", "").replace("}", "").split(",");
                for (String url : urls) {
                    if ((url.split("=")[0].trim()).equals(NORMAL_POSTER_SIZE)) {
                        adUrl = getLive_dir() + url.split("=")[1];
                        break;
                    }
                }
            } else {
                // 否则默认返回第一个海报
                adUrl = getRealtimeAdByPosterList(this);
            }
        }
        return adUrl;
    }

    public String getRealtimePostUrlBySize(String posterSize){
        String adUrl = null;
        if (getLive_dir() != null && getLive_list() != null) {
			/*
			 * getList={100x126=100x126_1.jpg, 113x138=113x138_1.jpg,
			 * 142x172=142x172_1.jpg}
			 */
            String adJson = getLive_list().toString().trim();
            if (adJson.contains(posterSize)) {
                String[] urls = adJson.replace("{", "").replace("}", "").split(",");
                for (String url : urls) {
                    if ((url.split("=")[0].trim()).equals(posterSize)) {
                        adUrl = getLive_dir() + url.split("=")[1];
                        break;
                    }
                }
            } else {
                // 否则返回正常尺寸
                Log.d("PosterList", "can't find postsize " + posterSize + ",getRealtimePostUrl");
                adUrl = getRealtimePostUrl();
            }
        }
        return adUrl;
    }

    /*
     * 获取第二尺寸海报，视频监控海报
     */
    public String getSecondPostUrl(){
        return getAdByPosterList(this,2);
    }

    /*
     * 内部调用
     */
    private static String getAdByPosterList(PosterList posterList, int index){
        String adUrl = null;
        if(posterList.getDir()!=null&&posterList.getList()!=null){
//			printLog( "getAdByPosterList getList="+posterList.getList().toString());
            String adJson = posterList.getList().toString();
            adJson = getAdUrl(adJson,index);
            adUrl = posterList.getDir() + adJson;
        }
        printLog( "getAdByPosterList adUrl="+adUrl);
        return adUrl;
    }

    private static String getAdUrl(String adJson,int index){
//		if(adJson.contains(",")){
//			adJson = adJson.replace("{", "").replace("}", "").split(",")[index];
//		}
        if(adJson.contains("=")){
            adJson = adJson.replace("{", "").replace("}", "").split("=")[index];
            adJson=adJson.split(",")[0];
            return adJson;
        }
        return null;
    }

    /*
     * 内部调用
     */
    private static String getAdByPosterList(PosterList posterList) {
        String adUrl = null;
        String adJson = posterList.getList().toString();

        adJson = getAdUrl(adJson);
        adUrl = posterList.getDir() + adJson;
        printLog( "getAdByPosterList adUrl=" + adUrl);
        return adUrl;
    }

    private static String getRealtimeAdByPosterList(PosterList posterList) {
        String adUrl = null;
        String adJson = posterList.getLive_list().toString();
        adJson = getAdUrl(adJson);
        adUrl = posterList.getLive_dir() + adJson + "&time=" + TimeUtils.getUTCtime();
        printLog( "getRealtimeAdByPosterList adUrl=" + adUrl);
        return adUrl;
    }

    private static String getAdUrl(String adJson) {
        if (adJson.contains("=")) {
            adJson = adJson.replace("{", "").replace("}", "").split("=")[1];
            adJson = adJson.split(",")[0];
            if(!TextUtils.isEmpty(adJson)&&adJson.contains("|"))
                adJson=adJson.split("\\|")[0];
//			adUrl = getDir() + url.split("=")[1];
            return adJson;
        }
        return null;
    }

    private static boolean DEBUG=false;
    //海报不是必须的，需要时再打印
    private static void printLog(String msg){
        if(DEBUG)
            Log.d("PosterListUtil", msg);
    }

    // public class PosterItem implements Serializable{
    // @Expose
    // @SerializedName("246x138")
    // private String poster_246x138;
    // @Expose
    // @SerializedName("640x338")
    // private String poster_640x338;
    // @Expose
    // @SerializedName("90x90")
    // private String poster_90x90;
    //
    // }
}
