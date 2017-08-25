package com.kegy.mobilemedia.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.kegy.mobilemedia.R;
import com.kegy.mobilemedia.controller.adapter.SearchAdapter;
import com.kegy.mobilemedia.model.media.SearchBean;
import com.kegy.mobilemedia.utils.Config;
import com.kegy.mobilemedia.utils.Logger;
import com.kegy.mobilemedia.utils.Toaster;
import com.kegy.mobilemedia.utils.common.JsonParser;
import com.kegy.mobilemedia.utils.http.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private TextView mSearchNoData, mSearch;
    private EditText mSearchStr;
    private ImageView mSearchIcon;
    private ListView mSearchResult;

    private ProgressBar mLoading;

    private String mUrl;
    private List<SearchBean.ItemData> mItemDatas = new ArrayList<>();
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private SearchAdapter mAdapter;

    public static Intent newIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchStr = (EditText) findViewById(R.id.tv_activity_search_search);
        mSearchNoData = (TextView) findViewById(R.id.tv_no_result);
        mSearchIcon = (ImageView) findViewById(R.id.iv_search_icon);
        mSearch = (TextView) findViewById(R.id.tv_begin_search);
        mSearchResult = (ListView) findViewById(R.id.lv_search_result);
        mSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchBean.ItemData item = (SearchBean.ItemData) mAdapter.getItem(position);
                startActivity(SystemVideoPlayerActivity.newIntent(SearchActivity.this,item.getDetailUrl()));
            }
        });
        mLoading = (ProgressBar) findViewById(R.id.pb_search);
        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchNoData.setVisibility(View.VISIBLE);
            }
        });
    }


    private void speechText() {
        //1.创建 SpeechSynthesizer 对象, 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
//2.合成参数设置，详见《 MSC Reference Manual》 SpeechSynthesizer 类
//设置发音人（更多在线发音人，用户可参见 附录13.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "vixr"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
//设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
//保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
//仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
//3.开始合成
        mTts.startSpeaking(mSearchStr.getText().toString(), mSynListener);
    }


    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时， error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
//percent为缓冲进度0~100， beginPos为缓冲音频在文本中开始位置， endPos表示缓冲音频在
        //文本中结束位置， info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
//percent为播放进度0~100,beginPos为播放音频在文本中开始位置， endPos表示播放音频在文
        //本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };


    private void showDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * @param recognizerResult
         * @param b                是否说话结束
         */
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String result = recognizerResult.getResultString();
            Logger.e("result ==" + result);
            String text = JsonParser.parseIatResult(result);
            //解析好的
            Logger.e("text ==" + text);

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(recognizerResult.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();//拼成一句
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            mSearchStr.setText(resultBuffer.toString());
            mSearchStr.setSelection(mSearchStr.length());
            searchText();
        }

        /**
         * 出错了
         *
         * @param speechError
         */
        @Override
        public void onError(SpeechError speechError) {
            Logger.d("onError ==" + speechError.getMessage());
        }
    }

    private void searchText() {
        String text = mSearchStr.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            if (mItemDatas != null && mItemDatas.size() > 0) {
                mItemDatas.clear();
            }
            try {
                text = URLEncoder.encode(text, "UTF-8");
                mUrl = Config.SEARCH_URL + text;
                getDataFromNet();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromNet() {
        mLoading.setVisibility(View.VISIBLE);
        HttpUtils.callJSONAPI(mUrl, new HttpUtils.StringResponseListener() {
            @Override
            public void onSuccess(final String result) {
                if (result != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processData(result);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                Logger.d("onError " + error);
            }
        });
    }

    private void processData(String data) {
        SearchBean searchBean = new Gson().fromJson(data, SearchBean.class);
        mItemDatas = searchBean.getItems();
        showData();
    }

    private void showData() {
        if (mItemDatas != null && mItemDatas.size() > 0) {
            mAdapter = new SearchAdapter(this, mItemDatas);
            mSearchResult.setAdapter(mAdapter);
            mSearchNoData.setVisibility(View.GONE);
        } else {
            mSearchNoData.setVisibility(View.VISIBLE);
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
        mLoading.setVisibility(View.GONE);
    }


    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Toaster.toast(SearchActivity.this, "初始化失败");
            }
        }
    }
}
