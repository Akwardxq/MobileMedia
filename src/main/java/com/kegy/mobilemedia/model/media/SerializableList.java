package com.kegy.mobilemedia.model.media;

import java.io.Serializable;
import java.util.List;


/**
 * Created by kegy on 2017/8/9.
 */

public class SerializableList<T> implements Serializable {

    private List<T> mList;

    public void setList(List<T> list) {
        this.mList = list;
    }

    public List<T> getList() {
        return mList;
    }

}
