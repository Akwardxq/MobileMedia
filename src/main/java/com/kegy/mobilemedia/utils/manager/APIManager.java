package com.kegy.mobilemedia.utils.manager;

import android.text.TextUtils;

import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.http.AsyncHttpClient;
import com.kegy.mobilemedia.utils.http.HttpUtils;
import com.kegy.mobilemedia.utils.http.RequestParams;

/**
 * Created by Administrator on 2017/8/24.
 */

public final class APIManager {

    private static final String SERVER_ACCESS = "http://access.homed.me:12690/";

    public static final String SERVER_SLAVE = "http://slave.homed.me:13160/";

    public static final String LOGIN_API_V1 = SERVER_ACCESS + "account/login";

    public static final String GET_TYPE_LIST = SERVER_SLAVE + "homed/programtype/get_list";

    /**
     * 根据分类获取不同的节目	homed/program/get_list
     *
     * @param label    分类id，多个分类id用“|”分割,0表示全部
     * @param pageidx  当前第几页，取值从1开始
     * @param pagenum  翻页参数，每页的总个数
     * @param asc      可选	1：升序，默认；0：降序
     * @param sortby   排序依据， 默认不传时给后台手动排序好的。取值
     *                 0：节目名称
     *                 1：热度
     *                 2：点播次数
     *                 3：评分
     *                 4：上映时间
     *                 5：后台发布时间
     * @param listener 回调处理
     */
    public static void getProgramList(String label, int pageidx, int pagenum, String asc, String sortby, HttpUtils.StringResponseListener listener) {
        String url = "http://slave.homed.me:13160/homed/program/get_list";
        RequestParams params = new RequestParams();
        params.put("accesstoken", MobileDataManager.getAccessToken());
        params.put("pageidx", "" + pageidx);
        params.put("pagenum", "" + pagenum);
        params.put("label", label);
        params.put("accesstoken", MobileDataManager.getAccessToken());
        params.put("sdsize", Config.NORMAL_POSTER_SIZE + "|" + Config.TOP_POSTER_SIZE + "|" + Config.VERTICAL_POSTER_SIZE + "|" + Config.PAD_NORMAL_POSTER_SIZE + "|" + Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("hdsize", Config.NORMAL_POSTER_SIZE + "|" + Config.TOP_POSTER_SIZE + "|" + Config.VERTICAL_POSTER_SIZE + "|" + Config.PAD_NORMAL_POSTER_SIZE + "|" + Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("vodsize", Config.NORMAL_POSTER_SIZE + "|" + Config.TOP_POSTER_SIZE + "|" + Config.VERTICAL_POSTER_SIZE + "|" + Config.PAD_NORMAL_POSTER_SIZE + "|" + Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("chnlsize", Config.CHANNEL_POSTER_SIZE + "|" + Config.NORMAL_POSTER_SIZE + "|" + Config.TOP_POSTER_SIZE + "|" + Config.PAD_NORMAL_POSTER_SIZE + "|" + Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("appsize", Config.NORMAL_POSTER_SIZE + "|" + Config.TOP_POSTER_SIZE + "|" + Config.VERTICAL_POSTER_SIZE + "|" + Config.PAD_NORMAL_POSTER_SIZE + "|" + Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("livesize", Config.NORMAL_POSTER_SIZE + "|" + Config.TOP_POSTER_SIZE + "|" + Config.VERTICAL_POSTER_SIZE + "|" + Config.PAD_NORMAL_POSTER_SIZE + "|" + Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("musicsize", Config.NORMAL_POSTER_SIZE + "|" + Config.TOP_POSTER_SIZE + "|" + Config.VERTICAL_POSTER_SIZE + "|" + Config.PAD_NORMAL_POSTER_SIZE + "|" + Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("datatype", "2");
        if (!TextUtils.isEmpty(sortby))
            params.put("sortby", sortby);
        if (!TextUtils.isEmpty(asc))
            params.put("asc", asc);
        String key = AsyncHttpClient.getUrlWithQueryString(url, params);
        HttpUtils.callJSONAPI(key, listener);
    }

    /**
     * 获取单series的详细信息
     *
     * @param series_id          剧集id
     * @param pageidx            翻页参数：第几页，取值从1开始
     * @param pagenum            翻页参数：每页多少个
     * @param onResponseListener
     */
    public static void getSeriesInfo(String series_id, int pageidx, int pagenum, HttpUtils.StringResponseListener onResponseListener) {
        String url = "http://slave.homed.me:13160/media/series/get_info";
        RequestParams params = new RequestParams();
        params.put("accesstoken", MobileDataManager.getAccessToken());
        params.put("seriesid", series_id);
        params.put("pageidx", pageidx + "");
        params.put("pagenum", pagenum + "");
        params.put("postersize", Config.NORMAL_POSTER_SIZE);
        params.put("deviceid", MobileDataManager.getDeviceId());
        String key = AsyncHttpClient.getUrlWithQueryString(url, params);
        HttpUtils.callJSONAPI(key, onResponseListener);
    }

    /**
     * 获取视频的详细信息
     *
     * @param videoid            视频id
     * @param onResponseListener
     */
    public static void getVideoInfo(String videoid, HttpUtils.StringResponseListener onResponseListener) {
        String url = Config.SERVER_SLAVE1 + "media/video/get_info";
        RequestParams params = new RequestParams();
        params.put("accesstoken", MobileDataManager.getAccessToken());
        params.put("videoid", videoid);
        params.put("verifycode", MobileDataManager.getDeviceId());
        HttpUtils.callJSONAPI(url, params, onResponseListener);
    }

    /**
     * 获取排行榜数据新接口
     *
     * @param contenttype 数值型，必选，排行分类，同媒资类型，取值见boss标准字段类型定义,目前只能取：直播（1101），电视剧（1100），电影（1101），综艺（1102），资讯（1104），教育（1107），取值为0时表示混合类型
     * @param accordtype  数值型，必选，排行统计依据。取值0，代表按综合因素排行(目前为点击量和上映时间的结合)；取值1，代表按点击量排行；取值2，代表按有效搜索量排行
     * @param period      数值型，必选，排行榜统计时段。取值1，代表日榜；取值2，代表周榜。
     * @param num         数值型，必选，请求节目数。
     * @param isdesc      数值型，可选，是否返回简介，取值0：不返回描述信息，1：返回描述信息，默认取值1返回。
     * @param postersize  字符串，可选，海报尺寸，多个海报尺寸用”|”分隔，取值如”200x300|100x200”，不传默认返回全部海报。
     * @param datatype    数值型，可选，返回数据类型，取值1：返回单集；2：返回剧集，不传默认取值为2，返回剧集
     */
    public static void getRankList(String contenttype, String accordtype, String period, String num, String isdesc,
                            String postersize, String datatype, HttpUtils.StringResponseListener stringResponseListener) {
        String url = Config.SERVER_SLAVE1 + "rank/get_list";
        RequestParams params = new RequestParams();
        params.put("accesstoken", MobileDataManager.getAccessToken());
        params.put("contenttype", contenttype);
        params.put("accordtype", accordtype);
        params.put("period", period);
        params.put("num", num);
        if (!TextUtils.isEmpty(isdesc))
            params.put("isdesc", isdesc);
        if (!TextUtils.isEmpty(postersize))
            params.put("postersize", postersize);
        if (!TextUtils.isEmpty(datatype))
            params.put("datatype", datatype);
        HttpUtils.callJSONAPI(url, params, stringResponseListener);
    }

    /**
     * 根据用户指定的类型从排行榜获取推荐	recommend/get_top_recommend
     * @param label	节目分类，不同类型之间用“|”分割,取值为“0”,表示所有类型推荐
     * @param num 类推荐的个数，多个用“|”分割，与label一一对应
     * @param stringResponseListener 回调处理
     */
    public static void getTopRecommend(String label,String num,HttpUtils.StringResponseListener stringResponseListener){
        String url = Config.SERVER_SLAVE + "recommend/get_top_recommend";
        RequestParams params = new RequestParams();
        params.put("label", label);
        params.put("num", num);
        params.put("sdsize", Config.NORMAL_POSTER_SIZE+"|"+Config.TOP_POSTER_SIZE+"|"+Config.VERTICAL_POSTER_SIZE+"|"+Config.PAD_NORMAL_POSTER_SIZE+"|"+Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("hdsize", Config.NORMAL_POSTER_SIZE+"|"+Config.TOP_POSTER_SIZE+"|"+Config.VERTICAL_POSTER_SIZE+"|"+Config.PAD_NORMAL_POSTER_SIZE+"|"+Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("vodsize", Config.NORMAL_POSTER_SIZE+"|"+Config.TOP_POSTER_SIZE+"|"+Config.VERTICAL_POSTER_SIZE+"|"+Config.PAD_NORMAL_POSTER_SIZE+"|"+Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("chnlsize", Config.CHANNEL_POSTER_SIZE+"|"+Config.NORMAL_POSTER_SIZE+"|"+Config.TOP_POSTER_SIZE+"|"+Config.VERTICAL_POSTER_SIZE+"|"+Config.PAD_NORMAL_POSTER_SIZE+"|"+Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("appsize", Config.NORMAL_POSTER_SIZE+"|"+Config.TOP_POSTER_SIZE+"|"+Config.VERTICAL_POSTER_SIZE+"|"+Config.PAD_NORMAL_POSTER_SIZE+"|"+Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("livesize", Config.NORMAL_POSTER_SIZE+"|"+Config.TOP_POSTER_SIZE+"|"+Config.VERTICAL_POSTER_SIZE+"|"+Config.PAD_NORMAL_POSTER_SIZE+"|"+Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("musicsize", Config.NORMAL_POSTER_SIZE+"|"+Config.TOP_POSTER_SIZE+"|"+Config.VERTICAL_POSTER_SIZE+"|"+Config.PAD_NORMAL_POSTER_SIZE+"|"+Config.PAD_VERTICAL_POSTER_SIZE);
        params.put("accesstoken", MobileDataManager.getAccessToken());
        params.put("datatype", "2");
        HttpUtils.callJSONAPI( url, params,stringResponseListener);
    }


}
