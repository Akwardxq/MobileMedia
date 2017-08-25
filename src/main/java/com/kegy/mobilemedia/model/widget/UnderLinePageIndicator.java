/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kegy.mobilemedia.model.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.Logger;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class UnderLinePageIndicator extends HorizontalScrollView implements PageIndicator {
	/** Title text used when no title is provided by the adapter. */
	private static final CharSequence EMPTY_TITLE = "";

	/**
	 * Interface for a callback when the selected tab has been reselected.
	 */
	public interface OnTabReselectedListener {
		/**
		 * Callback when the selected tab has been reselected.
		 * 
		 * @param position
		 *            Position of the current center item.
		 */
		void onTabReselected(int position);
	}

	private Runnable mTabSelector;

	private int selectedTextColor = Color.parseColor("#F28300");
	private int unSelectedTextColor = Color.BLACK;
	private int tabWidth = 0;

	private OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View view) {
			TabView tabView = (TabView) view;
			final int oldSelected = mViewPager.getCurrentItem();
			final int newSelected = tabView.getIndex();
//			mViewPager.setCurrentItem(newSelected);
			if (oldSelected == newSelected && mTabReselectedListener != null) {
				mTabReselectedListener.onTabReselected(newSelected);
			} 
			
			if(oldSelected !=newSelected){
				tabView.view.setVisibility(View.VISIBLE);
				TabView oldTabView = (TabView) mTabLayout.getChildAt(oldSelected);
				oldTabView.view.setVisibility(View.GONE);
			}
	        //让当前标签总是显示在第二个位置  
	        drawUnderline(newSelected);  
	        mViewPager.setCurrentItem(newSelected, false);  
		}
	};

	private final IcsLinearLayout mTabLayout;

	private ViewPager mViewPager;
	private OnPageChangeListener mListener;

	private int mMaxTabWidth;
	private int mSelectedTabIndex;

	private OnTabReselectedListener mTabReselectedListener;

	public UnderLinePageIndicator(Context context) {
		this(context, null);
	}

	public UnderLinePageIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UnderLinePageIndicator(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		
		setHorizontalScrollBarEnabled(false);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabPageIndicator, defStyle, 0);

		selectedTextColor = a.getColor(R.styleable.TabPageIndicator_selectedTextColor,
				Color.parseColor("#F28300"));
		unSelectedTextColor = a.getColor(R.styleable.TabPageIndicator_unSelectedTextColor,
				Color.BLACK);
		tabWidth = (int) a.getDimension(R.styleable.TabPageIndicator_tagWidth, 0);
		mTabLayout = new IcsLinearLayout(context, R.attr.vpiTabPageIndicatorStyle);

		addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
		
		//用来绘制滚动的下划线
		mPaint = new Paint();  
        mPaint.setColor(selectedTextColor); 
        mPaint.setStrokeWidth(lineHeight);

		setWillNotDraw(false);
	}
	
	private Paint mPaint; 
	private boolean isDrawOK;
	private float mTranslateX; 
	private int lineHeight = 10;//默认下划线的宽度
	@Override  
    protected void dispatchDraw(Canvas canvas) {  
        super.dispatchDraw(canvas);  
        //改变下划线的位置  
		if (mViewPager != null) {
			canvas.translate(mTranslateX, 0);
			TabView tabView = (TabView) mTabLayout.getChildAt(mViewPager
					.getCurrentItem());
			// 绘制下划线
			if (tabView != null) {
				canvas.drawLine(0, tabView.getHeight(), tabView.getWidth(),
						tabView.getHeight(), mPaint);
			}
		}
        
    }  
	/** 
     * 设置下划线的位置 
     * @param pos ViewPager对应的索引位置 
     * @param posOffset ViewPager滑动百分比 
     */  
    private void drawUnderline(int pos, float posOffset) {  
        TabView tabView = (TabView) mTabLayout.getChildAt(pos);
        if(tabView!=null){
            mTranslateX = tabView.getX() + posOffset * tabView.getWidth();
            invalidate();  
        }
    }  
    /** 
     * 设置下划线的位置 
     * @param pos ViewPager对应的索引位置 
     */  
    private void drawUnderline(int pos) {  
    	TabView tabView = (TabView) mTabLayout.getChildAt(pos);
    	if(tabView!=null){
    	    mTranslateX = tabView.getX();
    	    invalidate();  
        }
    }  

	public void setOnTabReselectedListener(OnTabReselectedListener listener) {
		mTabReselectedListener = listener;
	}

	public void setOnTabClickListener(OnClickListener listener) {
		mTabClickListener = listener;
		notifyDataSetChanged();
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
		setFillViewport(lockedExpanded);

		final int childCount = mTabLayout.getChildCount();
		if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
			if (childCount > 2) {
				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f); // 恰好一行四个
			} else {
				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) / 2);
			}
		} else {
			mMaxTabWidth = -1;
		}

		final int oldWidth = getMeasuredWidth();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int newWidth = getMeasuredWidth();

		if (lockedExpanded && oldWidth != newWidth && mViewPager != null) {
			// Recenter the tab display if we're at a new (scrollable) size.
			setCurrentItem(mSelectedTabIndex);
		}

	}

	private void animateToTab(final int position) {
		final TabView tabView = (TabView) mTabLayout.getChildAt(position);
		if(tabView!=null){
		    if (mTabSelector != null) {
		        removeCallbacks(mTabSelector);
		     }
		    mTabSelector = new Runnable() {
		        public void run() {
		            final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
		            smoothScrollTo(scrollPos, 0);
		            mTabSelector = null;
		            }
		    };
		    post(mTabSelector);
		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mTabSelector != null) {
			// Re-post the selector we saved
			post(mTabSelector);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
	}

	private void addTab(int index, CharSequence text, int iconResId, boolean userStroke) {
		final TabView tabView = new TabView(getContext());
		tabView.mIndex = index;
		tabView.setFocusable(true);
		tabView.setHeight((int) (Config.dp2px(43)));
		tabView.setOnClickListener(mTabClickListener);
		tabView.setText(text);
		if (tabWidth == 0)
			mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
		else
			mTabLayout.addView(tabView, new LinearLayout.LayoutParams(tabWidth, MATCH_PARENT));

		if (iconResId != 0) {
			View view = new View(getContext());
			view.setBackgroundColor(Color.GRAY);
			tabView.addView(tabView, new LinearLayout.LayoutParams(1, (int) (Config.dp2px(43)), 0));

		}
		if (userStroke) {

			// tabView.text.setBackground(getContext().getResources().getDrawable(R.drawable.bg_tab_underline_indicator));
			if (index == mSelectedTabIndex) {
				tabView.view.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		drawUnderline(arg0, arg1);  
        //实现标签与下划线一起滚动的效果  
		if (mListener != null) {
			mListener.onPageScrolled(arg0, arg1, arg2);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurrentItem(arg0);
		if (mListener != null) {
			mListener.onPageSelected(arg0);
		}
	}

	@Override
	public void setViewPager(ViewPager view) {
		Logger.d("set ViewPager");
		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);
		}
		final PagerAdapter adapter = view.getAdapter();
		if (adapter == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}
		mViewPager = view;
		view.setOnPageChangeListener(this);
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
        Logger.d("notify data set change");
		mTabLayout.removeAllViews();
		PagerAdapter adapter = mViewPager.getAdapter();
		IconPagerAdapter iconAdapter = null;
		if (adapter instanceof IconPagerAdapter) {
			iconAdapter = (IconPagerAdapter) adapter;
		}
		final int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			CharSequence title = adapter.getPageTitle(i);
			if (title == null) {
				title = EMPTY_TITLE;
			}
			int iconResId = 0;
			boolean useStroke = false;
			if (iconAdapter != null) {
				iconResId = iconAdapter.getIconResId(i);
				useStroke = iconAdapter.useStroke(i);
			}
			addTab(i, title, iconResId, useStroke);
		}
		if (mSelectedTabIndex > count) {
			mSelectedTabIndex = count - 1;
		}
		setCurrentItem(mSelectedTabIndex);
		requestLayout();
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		// PagerAdapter adapter = view.getAdapter();
		// IconPagerAdapter iconAdapter = null;
		// if (adapter instanceof IconPagerAdapter) {
		// iconAdapter = (IconPagerAdapter)adapter;
		// }
		// if(iconAdapter!=null)
		// {
		// Boolean hasIcon=iconAdapter.getIconView(initialPosition);
		//
		// }
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
		if (mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		mSelectedTabIndex = item;
		mViewPager.setCurrentItem(item);

		final int tabCount = mTabLayout.getChildCount();
		for (int i = 0; i < tabCount; i++) {
			final View child = mTabLayout.getChildAt(i);

			final boolean isSelected = (i == item);
			child.setSelected(isSelected);

			if (isSelected) {
				if (child instanceof TabView) {
					((TabView) child).text.setTextColor(selectedTextColor);
					((TabView) child).view.setVisibility(View.VISIBLE);
				}
				animateToTab(item);
			} else {
				if (child instanceof TabView) {
					((TabView) child).text.setTextColor(unSelectedTextColor);
					((TabView) child).view.setVisibility(View.GONE);
				}

			}
		}
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}

	@SuppressLint("ResourceAsColor")
    public class TabView extends LinearLayout {
		private int mIndex;
		TextView text;
		View view;

		public TabView(Context context) {
			super(context, null, 0);

			setOrientation(LinearLayout.VERTICAL);

			text = new TextView(context, null, R.attr.vpiTabPageIndicatorStyle);
			text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			text.setDuplicateParentStateEnabled(true);
			text.setSingleLine(true);
			text.setTextSize(18);
			text.setTextColor(unSelectedTextColor);
			addView(text);

			view = new View(context);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 5));
//			view.setBackgroundColor(selectedTextColor);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setVisibility(View.GONE);

			addView(view);

			setPadding((int) Config.dp2px(5), 0, (int) Config.dp2px(5), 0);
		}

		public void setText(CharSequence text2) {
			text.setText(text2);

		}

		public void setHeight(int i) {
			text.setHeight(i);

		}

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			if (tabWidth != 0)
				return;
			// Re-measure if we went beyond our maximum size.
			if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
				super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
			}
		}

		public int getIndex() {
			return mIndex;
		}
	}
}
