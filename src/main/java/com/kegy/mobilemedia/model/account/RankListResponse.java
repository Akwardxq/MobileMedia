package com.kegy.mobilemedia.model.account;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.kegy.mobilemedia.controller.base.BaseResponse;
import com.kegy.mobilemedia.model.program.PosterList;

import java.io.Serializable;
import java.util.List;

public class RankListResponse extends BaseResponse {

    @SerializedName("list")
    public List<RankListItem> list;

    public class RankListItem implements Serializable {
        public String id;

        public int type;

        public String name;

        public long times;

        public String series_id;

        public int series_total;

        public String current_idx;

        public String series_idx;

        public String desc;

        public int is_purchased;

        public PosterList poster_list;


        public String getShowCurrent_idx() {
            String result = "";
            if (TextUtils.isEmpty(current_idx) || current_idx.equals("0"))  //与ios同步处理，current_idx为0时取series_idx
                current_idx = series_idx;
            if (current_idx != null && TextUtils.isDigitsOnly(current_idx)) {
                if (current_idx.length() < 8)
                    result = current_idx + "集";
                else if (current_idx.length() == 8) {
                    result = current_idx + "期";
                } else {
                    long second = Long.parseLong(current_idx);
//                    result = TimeHelper.getDate_f(second);
                }
            } else
                result = current_idx;
            return result;
        }
    }

}
