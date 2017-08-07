package jinxin.out.com.jinxinhospital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.PurchaseRecord.PurchaseRecord;
import jinxin.out.com.jinxinhospital.PurchaseRecord.PurchaseRecordResponseJson;
import jinxin.out.com.jinxinhospital.PurchaseRecord.PurchaseResponseData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/18.
 */

public class HealthManageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private View mView;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeLayout;
    private MainActivity mContext;
    private MyAdapter myAdapter;
    private MyHandler mMainHandler;

    private List<PurchaseResponseData> mPurchaseContentRecord = new ArrayList<>();
    private PurchaseRecordResponseJson mPurchaseRecordResponseJson;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.healthmanager_layout, container, false);
        mListView = mView.findViewById(R.id.health_listview);
        mSwipeLayout = mView.findViewById(R.id.my_cus_refresh);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(300);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(R.color.white); // 设定下拉圆圈的背景
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);

        new Thread() {
            @Override
            public void run() {
                String token = LoadActivity.getToken();
                int customerId = LoadActivity.getmCustomerId();
                Log.d("xie", "token = " + token);
                Log.d("xie", "customerId = " + customerId);
                if (token == "" || customerId <0){
                    Intent intent = new Intent(mContext, LoadActivity.class);
                    startActivity(intent);
                    return;
                }
                RequestBody requestBody = new FormBody.Builder()
                        .add("token", token)
                        .add("customerId", customerId+"")
                        .build();
                NetPostUtil.post(Constants.GET_PURCHASE_WITH_ID, requestBody, mHealthManagerCallback);
            }
        }.start();
        mListView.setAdapter(myAdapter);
        mMainHandler = new MyHandler(mContext);
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        mContext = (MainActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onRefresh() {

    }
    public class MyAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView name;
            public TextView status;
            public TextView time;
            public TextView used;
            public TextView total;
            public TextView actual;
            public Button check;
        }

        @Override
        public int getCount() {
            return mPurchaseContentRecord.size();
        }

        @Override
        public Object getItem(int i) {
            return mPurchaseContentRecord.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.purchase_item, viewGroup, false);
                holder = new ViewHolder();
                holder.name = view.findViewById(R.id.projectName);
                holder.time = view.findViewById(R.id.projectTime);
                holder.used = view.findViewById(R.id.usedEdit);
                holder.total = view.findViewById(R.id.totalEdit);
                holder.actual = view.findViewById(R.id.actulEdit);
                holder.check = view.findViewById(R.id.health_check);
                holder.status = view.findViewById(R.id.health_status);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            PurchaseResponseData data = mPurchaseContentRecord.get(i);
            holder.name.setText(data.projectName);
//            holder.time.setText(JsonUtil.getDate(data.t));
            holder.time.setText("xxxxxxxxxxxx");
            holder.used.setText(data.useFrequency);
            holder.actual.setText("xxx");
            holder.total.setText("xxx");
            String status = "";
            switch (data.status) {
                case 1:
                    status = "可用";
                    break;
                case 2:
                    status = "完成";
                    break;
                case 3:
                    status = "过期";
                    break;
                case 4:
                    status = "退费";
                    break;
                case 5:
                    status = "作废";
                    break;
            }
            holder.status.setText(status);
            holder.check.setOnClickListener(mCheckMINXIClick);
            return view;
        }
    }

    private View.OnClickListener mCheckMINXIClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private Callback mHealthManagerCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

            String result = response.body().string();
            mPurchaseContentRecord.clear();
            Log.d("xie", "mmm"+result);

            mPurchaseRecordResponseJson =
                    JsonUtil.parsoJsonWithGson(result, PurchaseRecordResponseJson.class);

            if (mPurchaseRecordResponseJson.code != 0) {
                return;
            }
//            mPurchaseContentRecord.addAll(((PurchaseRecordResponseJson) mPurchaseRecordResponseJson).data);
//            mPurchaseContentRecord = mPurchaseRecordResponseJson.data;
            mMainHandler.sendEmptyMessage(222);
        }
    };

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:
                    mSwipeLayout.setRefreshing(false);
                    break;
                case 222:
                    myAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }
}
