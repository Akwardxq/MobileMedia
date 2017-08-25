package com.kegy.mobilemedia.controller.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.utils.Toaster;

/**
 * Created by kegy on 2017/8/8.
 */

public class HeaderView extends LinearLayout implements View.OnClickListener {

    private Context mContext;

    private View mSearchView;
    private View mGameView;
    private View mHistoryView;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSearchView = findViewById(R.id.tv_search);
        mGameView = findViewById(R.id.tv_game);
        mHistoryView = findViewById(R.id.iv_history);
        mSearchView.setOnClickListener(this);
        mGameView.setOnClickListener(this);
        mHistoryView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                Toaster.toast(mContext,"搜索");
                break;
            case R.id.tv_game:
                Toaster.toast(mContext,"游戏");
                break;
            case R.id.iv_history:
                Toaster.toast(mContext,"历史记录");
                break;
        }
    }
}
