package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import jinxin.out.com.jinxinhospital.VIP.VipData;
import jinxin.out.com.jinxinhospital.VIP.VipPowerResponseJson;
import jinxin.out.com.jinxinhospital.VIP.VipResponseJson;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin on 2017/8/15.
 */

public class DetailsActivity extends UserAppCompatActivity {
    private View mView;
    private TextView mWebView;
    private String mTitle;
    private String mPath;
    private News mNews = new News();
    private Employee mEmployee = new Employee();
    private String mMessage = "";
    private String url = "";
    private MyHandler myHandler;
    private TextView mTextView;
    private Context mContext;
    private String mTitleMsg = "详请介绍";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        myHandler = new MyHandler(mContext);
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getInt("id") + "";
        String type = bundle.getString("type", "");
        Log.d("xie" , "type = " + type);
        if ("news".equals(type)) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("id",id )
                    .build();
            NetPostUtil.post(Constants.GET_NEWS_CONTENT_WITH_ID, requestBody,mNewContentCallback);
        } else if ("employee".equals(type)) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("id",id )
                    .build();
            NetPostUtil.post(Constants.GET_EMPLOYEE_WITH_ID, requestBody,mEmployeeContentCallback);
        } else if ("vip".equals(type)) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
            RequestBody requestBody = new FormBody.Builder()
                    .add("token", sharedPreferences.getString("token", null))
                    .add("customerId", sharedPreferences.getInt("customerId", -1) + "")
                    .add("id", id + "")
                    .build();
            NetPostUtil.post(Constants.GET_VIP_MESSAGE_CONTENT, requestBody, mVipContentCallback);
        } else if ("vip_power".equals(type)) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
            RequestBody requestBody = new FormBody.Builder()
                    .add("token", sharedPreferences.getString("token", null))
                    .add("customerId", sharedPreferences.getInt("customerId", -1) + "")
                    .build();
            NetPostUtil.post(Constants.GET_VIP_PRIVILEGE, requestBody, mVipPowerContentCallback);
        }
        mTextView = findViewById(R.id.news_content_message);
        mWebView = findViewById(R.id.webView);
        setToolBarTitle(mTitleMsg);
    }

    private Callback mVipContentCallback = new Callback() {
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
                url = module.message;
                Log.d("xie", "health: mMessage = " + mMessage);
                myHandler.sendEmptyMessage(1);
                return;
            }
            VipResponseJson vipResponseJson = JsonUtil.parsoJsonWithGson(result,
                    VipResponseJson.class);
            if (vipResponseJson != null) {
                VipData vipData = vipResponseJson.data[0];
                url = vipData.content;
                mTitleMsg = vipData.title;
            }
            myHandler.sendEmptyMessage(1);
        }
    };

    private Callback mVipPowerContentCallback = new Callback() {
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
                url = module.message;
                Log.d("xie", "health: mMessage = " + mMessage);
                myHandler.sendEmptyMessage(1);
                return;
            }
            VipPowerResponseJson vipResponseJson = JsonUtil.parsoJsonWithGson(result,
                    VipPowerResponseJson.class);
            if (vipResponseJson != null) {
                VipData vipData = vipResponseJson.data;
                url = vipData.content;
                mTitleMsg = vipData.title;
            }
            myHandler.sendEmptyMessage(1);
        }
    };

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
                mTitleMsg = mEmployee.name;
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
                mTitleMsg = mNews.title;
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
            mTextView.setVisibility(View.GONE);
            setToolBarTitle(mTitleMsg);
            mWebView.setText(Html.fromHtml(url));
            mWebView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.news_content_layout;
    }
}
