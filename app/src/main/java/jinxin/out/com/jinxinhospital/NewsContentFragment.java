package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import jinxin.out.com.jinxinhospital.Employee.Employee;
import jinxin.out.com.jinxinhospital.Employee.EmployeeContentResponseJson;
import jinxin.out.com.jinxinhospital.Employee.EmployeeResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.News.News;
import jinxin.out.com.jinxinhospital.News.NewsContentResponseJson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/8.
 */

public class NewsContentFragment extends BaseFragment{
    private View mView;
    private WebView mWebView;
    private String mTitle;
    private String mPath;
    private News mNews = new News();
    private Employee mEmployee = new Employee();
    private String mMessage = "";
    private String url = "";
    private MyHandler myHandler;
    private TextView mTextView;
    private ImageView mBackView;
    private TextView mHeaderTitle;
    private MainActivity mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity)context;
        myHandler = new MyHandler(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.news_content_layout, container, false);

        Log.d("xie", "onCreateView222222222222222222222222222222222");
        Bundle bundle = (Bundle)getArguments();
        String id = bundle.getInt("id") + "";
        if (bundle.getBoolean("isNews", false)) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("id",id )
                    .build();
            NetPostUtil.post(Constants.GET_NEWS_CONTENT_WITH_ID, requestBody,mNewContentCallback);
        } else {
            RequestBody requestBody = new FormBody.Builder()
                    .add("id",id )
                    .build();
            NetPostUtil.post(Constants.GET_EMPLOYEE_WITH_ID, requestBody,mEmployeeContentCallback);
        }
        mTextView = mView.findViewById(R.id.news_content_message);
        mWebView = mView.findViewById(R.id.webView);
        mBackView = mView.findViewById(R.id.back);
        mBackView.setOnClickListener(mBackListener);
        mHeaderTitle = mView.findViewById(R.id.header_title);
        mHeaderTitle.setText("详请介绍");
        return mView;
    }

    private Callback mEmployeeContentCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("xie", "mNewContentCallback onFailure");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie", "mConsumptionListCallback: result"+result);
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code < 0) {
                mMessage = module.message;
                Log.d("xie", "health: mMessage = " + mMessage);
//                mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
                return;
            }
            EmployeeContentResponseJson employeeContentResponseJson = JsonUtil.parsoJsonWithGson(result,
                    EmployeeContentResponseJson.class);
            if (employeeContentResponseJson != null) {
                mEmployee = employeeContentResponseJson.data;
                url = mEmployee.introduction;
            }
            myHandler.sendEmptyMessage(1);
        }
    };

    private Callback mNewContentCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("xie", "mNewContentCallback onFailure");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie", "mConsumptionListCallback: result"+result);
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code < 0) {
                mMessage = module.message;
                Log.d("xie", "health: mMessage = " + mMessage);
//                mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
                return;
            }
            NewsContentResponseJson newsContentResponseJson = JsonUtil.parsoJsonWithGson(result,
                    NewsContentResponseJson.class);
            if (newsContentResponseJson != null) {
                mNews = newsContentResponseJson.data;
                url = mNews.content;
            }
            myHandler.sendEmptyMessage(1);
        }
    };

    private class MyHandler extends Handler {
        public MyHandler (Context mContext){
            super(mContext.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            Log.d("xie", "url = " + url.toString());
            url = "http://www.baidu.com";
            if (Patterns.WEB_URL.matcher(url).matches()) {
                //符合标准
                mTextView.setVisibility(View.GONE);
                mWebView.loadUrl(url);
            } else{
                mTextView.setVisibility(View.VISIBLE);
                //不符合标准
            }
        }
    }
}
