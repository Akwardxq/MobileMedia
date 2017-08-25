package com.kegy.mobilemedia.controller.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *Fragment的父类<br>
 *@author kegy
 */
public abstract class BaseFragment extends Fragment {

    protected View mContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(bindContentView(),container,false);
        initView();
        getData();
        return mContentView;
    }

    protected abstract int bindContentView();

    protected abstract void initView();

    protected abstract void getData();

    protected View findViewById(int id) {
        return mContentView.findViewById(id);
    }

}
