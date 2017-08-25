package com.kegy.mobilemedia.controller.fragment.page3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.base.BaseFragment;
import com.kegy.mobilemedia.controller.fragment.page2.SortedMediaPageFragment;
import com.kegy.mobilemedia.model.widget.UnderLinePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/24.
 */

public class LocalMediaFragment extends BaseFragment {

    private List<String> mTypes = new ArrayList<>();
    private UnderLinePageIndicator mIndicator;
    private ViewPager mViewPager;

    @Override
    protected int bindContentView() {
        return R.layout.fragment_sorted_media;//这个布局可以复用
    }

    @Override
    protected void initView() {
        mIndicator = (UnderLinePageIndicator) findViewById(R.id.upi_sorted_media);
        mViewPager = (ViewPager) findViewById(R.id.vp_sorted_media);
    }

    @Override
    protected void getData() {
        mTypes.add("视频");
        mTypes.add("音乐");
        mViewPager.setOffscreenPageLimit(mTypes.size());
        mViewPager.setAdapter(new TypePagerAdapter(getChildFragmentManager()));
        mIndicator.setViewPager(mViewPager);
    }

    private class TypePagerAdapter extends FragmentStatePagerAdapter {
        public TypePagerAdapter(FragmentManager childFragmentManager) {
            super(childFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return "视频".equals(getPageTitle(position))?LocalVideoPageFragment.newInstance():
                    LocalAudioPageFragment.newInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTypes.get(position);
        }

        @Override
        public int getCount() {
            return mTypes.size();
        }
    }
}
