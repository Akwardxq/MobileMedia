package com.kegy.mobilemedia.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.utils.Config;

public class SplashActivity extends AppCompatActivity {

    private static final int LOADING_MESSAGE = 0x120;
    private static final int LOADING_TIME_OUT = 3 * 1000;

    private int mCostTime = 0;

    private TextView mTextView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOADING_MESSAGE) {
                int restTime = LOADING_TIME_OUT - mCostTime;
                if (restTime == 0) {
                    toMainPage();
                } else {
                    mTextView.setText((restTime / 1000) + "s" + "跳过启动页>>");
                    mHandler.sendEmptyMessageDelayed(LOADING_MESSAGE, 1000);
                    mCostTime += 1000;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Config.dm = displayMetrics;

        mTextView = (TextView) findViewById(R.id.tv_activity_splash_jump_main);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainPage();
            }
        });
        mHandler.sendEmptyMessage(LOADING_MESSAGE);
    }

    private void toMainPage() {
        mHandler.removeMessages(LOADING_MESSAGE);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
