package jinxin.out.com.jinxinhospital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.PurchaseRecord.PurchaseRecordResponseJson;
import jinxin.out.com.jinxinhospital.PurchaseRecord.PurchaseResponseData;
import jinxin.out.com.jinxinhospital.PurchaseRecord.PurchaseVIPResponseJson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/18.
 */

public class HealthManageFragment extends BaseFragment{

    private static final int ADAPTER_DATA_CHANGE = 0x11;
    private static final int ADAPTER_DATA_REFRESH = 0x12;
    private static final int DISPLAY_TEXT = 0x13;
    private View mView;
    private ListView mListView;
    private ListView mPullRefreshListView;
    private MainActivity mContext;
    private MyAdapter myAdapter;
    private MyHandler mMainHandler;
    private TextView mTextview;
    private String mMessage = "";
    private String token;
    private int customerId;
    private double  mBalance;
    private TextView mBalanceText;
    private LinearLayout mBalanceLayout;
    private boolean isVip = false;

    private List<PurchaseResponseData> mPurchaseContentRecord = new ArrayList<>();
    private PurchaseRecordResponseJson mPurchaseRecordResponseJson;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.healthmanager_layout, container, false);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        isVip = sharedPreferences.getBoolean("vip",false);
        mTextview= mView.findViewById(R.id.health_message);
        mPullRefreshListView = mView.findViewById(R.id.my_custorm_layout_list);
        mMainHandler = new MyHandler(mContext);
        myAdapter = new MyAdapter();
        mBalanceText = mView.findViewById(R.id.health_balance);
        mBalanceLayout = mView.findViewById(R.id.balanceLayout);
        mPullRefreshListView.setAdapter(myAdapter);
        new Thread() {
            @Override
            public void run() {
                Log.d("xie", "token = " + token);
                Log.d("xie", "customerId = " + customerId);
                onRefreshData();
            }
        }.start();
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        isVip = sharedPreferences.getBoolean("vip",false);
        if (token == "" || token == null || customerId <0){
            return;
        }
    }

    private class MyAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView name;
            public TextView status;
            public TextView time;
            public TextView used;
            public TextView total;
            public TextView actual;
            public Button check;
            public TextView actulMsg;
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
            i = mPurchaseContentRecord.size() < i ? mPurchaseContentRecord.size() : i;
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
                holder.actulMsg = view.findViewById(R.id.actulMsg);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            PurchaseResponseData data = mPurchaseContentRecord.get(i);
            holder.name.setText(data.projectName);
            holder.time.setText(JsonUtil.getDate(data.createTime));
            holder.used.setText(data.useFrequency + "");
            if (!isVip) {
                holder.actual.setVisibility(View.GONE);
                holder.actulMsg.setVisibility(View.GONE);
            }
            holder.actual.setText(data.totalPrice + "");
            holder.total.setText(data.projectFrequency + "");
            holder.status.setText(data.statusName);
            holder.check.setOnClickListener(mCheckMINXIClick);
            holder.check.setTag(data);
            return view;
        }
    }

    private View.OnClickListener mCheckMINXIClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("xie", "mdsglsadgl......................");
            Intent intent = new Intent("android.intent.action.CONSUMPTIONRECORD");
            PurchaseResponseData mPurchaseResponseData = (PurchaseResponseData) view.getTag();
            Bundle bundle = new Bundle();
            bundle.putString("projectName", mPurchaseResponseData.projectName);
            bundle.putString("purchaseRecordId", mPurchaseResponseData.id+"");
            bundle.putString("remark", mPurchaseResponseData.remark);
            intent.putExtras(bundle);
            startActivity(intent);
//            consumptionListFragment.setArguments(bundle);
//            consumptionListFragment.mParentFragment = HealthManageFragment.this;
//            mContext.showContent(consumptionListFragment);
        }
    };

    private Callback mHealthManagerCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("xie", "mHealthManagerCallback onFailure");
            mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            mPurchaseContentRecord.clear();
            Log.d("xie", "mHealthManagerCallback: result"+result);
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code < 0) {
                mMessage = module.message;
                Log.d("xie", "health: mMessage = " + mMessage);
                mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
                return;
            }
            if (isVip) {
                PurchaseVIPResponseJson purchaseVIPResponseJson=
                        JsonUtil.parsoJsonWithGson(result, PurchaseVIPResponseJson.class);
                mBalance = purchaseVIPResponseJson.data.balance;
                for (int i = 0; i < purchaseVIPResponseJson.data.purchaseRecordList.length; i++) {
                    mPurchaseContentRecord.add(purchaseVIPResponseJson.data.purchaseRecordList[i]);
                }
            } else {
                PurchaseRecordResponseJson purchaseRecordResponseJson=
                        JsonUtil.parsoJsonWithGson(result, PurchaseRecordResponseJson.class);
                for (int i = 0; i < purchaseRecordResponseJson.data.purchaseRecordList.length; i++) {
                    mPurchaseContentRecord.add(purchaseRecordResponseJson.data.purchaseRecordList[i]);
                }
            }
            mMainHandler.sendEmptyMessage(ADAPTER_DATA_CHANGE);
        }
    };

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADAPTER_DATA_REFRESH:
                    mTextview.setVisibility(View.GONE);
                    break;
                case ADAPTER_DATA_CHANGE:
                    if (isVip) {
                        mBalanceLayout.setVisibility(View.VISIBLE);
                        mBalanceText.setText(String.valueOf(mBalance) + "元");
                    }
                    mTextview.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    break;
                case DISPLAY_TEXT:
                    mTextview.setVisibility(View.VISIBLE);
                    mTextview.setText(mMessage);
                    break;
                default:
                    break;
            }
        }
    }
    private void onRefreshData() {
        Log.d("xie", "PURCHASE: onRefreshData()");
        Log.d("xie", "token = " + token);
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("customerId", customerId+"")
                .build();
        NetPostUtil.post(Constants.GET_PURCHASE_WITH_ID, requestBody, mHealthManagerCallback);
    }
}
