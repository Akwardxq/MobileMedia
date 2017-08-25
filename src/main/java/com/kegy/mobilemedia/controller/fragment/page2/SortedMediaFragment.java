package com.kegy.mobilemedia.controller.fragment.page2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.kegy.mobilemedia.MediaApp;
import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.base.BaseFragment;
import com.kegy.mobilemedia.model.account.TypeList.TypeChildren;
import com.kegy.mobilemedia.model.widget.UnderLinePageIndicator;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络多媒体分类fragment<br>
 *     by kegy
 */
public class SortedMediaFragment extends BaseFragment {

    private List<Integer> mTypeIds = new ArrayList<>();
    private List<String> mTypeNames = new ArrayList<>();
    private TypeChildren mTypeChildren;
    private UnderLinePageIndicator mIndicator;
    private ViewPager mViewPager;
    private TextView mTextView;

    @Override
    protected int bindContentView() {
        return R.layout.fragment_sorted_media;
    }

    @Override
    protected void initView() {
        mTextView = (TextView) findViewById(R.id.tv_fragment_sort_media_notype);
        mIndicator = (UnderLinePageIndicator)findViewById(R.id.upi_sorted_media);
        mViewPager = (ViewPager) findViewById(R.id.vp_sorted_media);
    }

    @Override
    protected void getData() {
        mTypeChildren = MediaApp.sTypeChildren;
        if (mTypeChildren != null && mTypeChildren.getChildren() != null) {
            for (TypeChildren children : mTypeChildren.getChildren()) {
                if(children.getLabelPosition() == Config.LABEL_VOD
                        || children.getLabelPosition() == Config.LABEL_SERIES
                        || children.getLabelPosition() == Config.LABEL_MUSIC) {
                    mTypeNames.add(children.getName());
                    mTypeIds.add(children.getId());
                    Logger.d("id=" + children.getId() + ",name=" + children.getName());
                }
            }
            if (mTypeIds.size() > 0) {
                mViewPager.setOffscreenPageLimit(mTypeIds.size());
                mViewPager.setAdapter(new TypePagerAdapter(getChildFragmentManager()));
                mIndicator.setViewPager(mViewPager);
            }
        } else {
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    private class TypePagerAdapter extends FragmentStatePagerAdapter {
        public TypePagerAdapter(FragmentManager childFragmentManager) {
            super(childFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return SortedMediaPageFragment.newInstance(mTypeIds.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTypeNames.get(position);
        }

        @Override
        public int getCount() {
            return mTypeIds.size();
        }
    }
}
