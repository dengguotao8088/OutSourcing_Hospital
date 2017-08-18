package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;

import jinxin.out.com.jinxinhospital.CurrentCheck.Queue;
import jinxin.out.com.jinxinhospital.CurrentCheck.QueueInfoResponseJson;
import jinxin.out.com.jinxinhospital.CurrentCheck.QueueListResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/18.
 */

public class CurrentCheckFragment extends BaseFragment {
    private View view;
    private TextView mNum;
    private TextView mShouFa;
    private TextView mYiQi;
    private TextView mYuJia;
    private TableLayout mTableLayout;
    private String token;
    private int customerId;
    private TextView mEmptyMsg;

    private MyHandler mHandler;
    private Context mContext;
    private String[] mQueueInfo;
    private Queue[] mQueueData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandler = new MyHandler(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.currentcheck_layout, container, false);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        if (token == null) {
            Intent intent = new Intent(mContext, LoadActivity.class);
            startActivity(intent);
        }
        mNum = view.findViewById(R.id.current_num);
        mShouFa = view.findViewById(R.id.current_shoufa);
        mYiQi = view.findViewById(R.id.current_yiqi);
        mYuJia = view.findViewById(R.id.current_yujia);
        mTableLayout = view.findViewById(R.id.current_tab);
        mEmptyMsg = view.findViewById(R.id.curren_empty);

        mNum.setText(customerId + "");

        getDataFromHttp();
        return view;
    }

    private void getDataFromHttp(){
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("customerId", customerId + "")
                .build();
        NetPostUtil.post(Constants.GET_FEILD_QUEUE_LIST, requestBody, mQueueCallback);

        NetPostUtil.post(Constants.GET_CURRENT_PAGE, requestBody, mQueueListCallback);
    }

    private void addDataToTable() {
        int length = mQueueData.length;
        if (length == 0 ) {
            mEmptyMsg.setVisibility(View.VISIBLE);
            return;
        } else {
            mEmptyMsg.setVisibility(View.GONE);
        }
        for (int i = 0; i < length;i++){
            Queue item = mQueueData[i];
            View layout = LayoutInflater.from(mContext).inflate(R.layout.table_item,null);//布局打气筒获取单行对象
            TextView tab_1 = (TextView) layout.findViewById(R.id.current_tab_1);
            TextView tab_2 = (TextView) layout.findViewById(R.id.current_tab_2);
            TextView tab_3 = (TextView) layout.findViewById(R.id.current_tab_3);
            TextView tab_4 = (TextView) layout.findViewById(R.id.current_tab_4);

            tab_1.setText(item.projectName);
            tab_2.setText(item.remarks);
            tab_3.setText(item.statusName);
            tab_4.setText(item.empName);

            mTableLayout.addView(layout);//将这一行加入表格中
        }
    }

    private class MyHandler extends Handler {
        public  MyHandler (Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    Log.d("xie", "*****mQueueInfo = " + mQueueInfo);
                    mShouFa.setText(mQueueInfo[0]);
                    mYiQi.setText(mQueueInfo[1]);
                    mYuJia.setText(mQueueInfo[2]);
                    break;
                case 0x22:
                    addDataToTable();
                    break;
            }
        }
    }

    private Callback mQueueCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "mQueueCallback: result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            QueueInfoResponseJson queueInfoResponseJson = JsonUtil.parsoJsonWithGson(result, QueueInfoResponseJson.class);
            mQueueInfo = queueInfoResponseJson.data;
            mHandler.sendEmptyMessage(0x11);
        }
    };
    private Callback mQueueListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "mQueueCallback: result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            QueueListResponseJson queueListResponseJson = JsonUtil.parsoJsonWithGson(result, QueueListResponseJson.class);
            mQueueData = queueListResponseJson.data;
            mHandler.sendEmptyMessage(0x22);
        }
    };
}
