package com.kegy.mobilemedia.controller.fragment.page4;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.base.BaseFragment;

/**
 * Created by Administrator on 2017/8/24.
 */

public class PersonCenterFragment extends BaseFragment {

    private LinearLayout mLinearLayout;

    @Override
    protected int bindContentView() {
        return R.layout.fragment_person_center;
    }

    @Override
    protected void initView() {
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_about_mobile_video);
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void getData() {

    }
}
