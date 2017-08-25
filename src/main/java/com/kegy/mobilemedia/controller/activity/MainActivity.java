package com.kegy.mobilemedia.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.base.BaseFragment;
import com.kegy.mobilemedia.controller.fragment.page1.HomeRecommendFragment;
import com.kegy.mobilemedia.controller.fragment.page2.SortedMediaFragment;
import com.kegy.mobilemedia.controller.fragment.page3.LocalMediaFragment;
import com.kegy.mobilemedia.controller.fragment.page4.PersonCenterFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RadioGroup mRadioGroup;
    private List<BaseFragment> mFragments = new ArrayList<>();
    private int mCurrentId = 0;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mRadioGroup = (RadioGroup) findViewById(R.id.activity_main_rg);
        ((RadioButton) mRadioGroup.getChildAt(mCurrentId)).setChecked(true);

        initFragments();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.activity_main_home_recommend:
                        switchFragment(mCurrentId, 0);
                        break;
                    case R.id.activity_main_sort_medias:
                        switchFragment(mCurrentId, 1);
                        break;
                    case R.id.activity_main_local_medias:
                        switchFragment(mCurrentId, 2);
                        break;
                }
            }
        });
    }

    private void initFragments() {
        mFragments.add(new HomeRecommendFragment());
        mFragments.add(new SortedMediaFragment());
        mFragments.add(new LocalMediaFragment());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_fragment_container, mFragments.get(0))
                .hide(mFragments.get(0));
        ft.add(R.id.fl_fragment_container, mFragments.get(1))
                .hide(mFragments.get(1));
        ft.add(R.id.fl_fragment_container, mFragments.get(2))
                .hide(mFragments.get(2));

        ft.commitAllowingStateLoss();
        initUI();
    }

    private void initUI() {
        Fragment fragment = mFragments.get(0);
        getSupportFragmentManager().beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss();
        ((RadioButton)mRadioGroup.getChildAt(0)).setChecked(true);
        this.mCurrentId = 0;
    }

    private void switchFragment(int from, int to) {
        this.mCurrentId = to;
        Fragment fromFragment = mFragments.get(from);
        Fragment toFragment = mFragments.get(to);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!toFragment.isAdded()) {
            ft.add(R.id.fl_fragment_container, toFragment).hide(fromFragment).commitAllowingStateLoss();
        } else {
            ft.show(toFragment).hide(fromFragment).commitAllowingStateLoss();
        }
    }

    @Override
    protected void onDestroy() {
        mRadioGroup = null;
        mFragments = null;
        super.onDestroy();
    }
}
