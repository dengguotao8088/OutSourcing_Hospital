package jinxin.out.com.jinxinhospital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private PullToRefreshListView mPullRefreshListView;
    private MainActivity mContext;
    private MyAdapter myAdapter;
    private MyHandler mMainHandler;
    private TextView mTextview;
    private String mMessage = "";
    String token;
    int customerId;
    private RequestBody requestBody;

    private List<PurchaseResponseData> mPurchaseContentRecord = new ArrayList<>();
    private PurchaseRecordResponseJson mPurchaseRecordResponseJson;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.healthmanager_layout, container, false);
        mTextview= mView.findViewById(R.id.health_message);
        mPullRefreshListView = mView.findViewById(R.id.my_custorm_layout_list);
        initPTRListView();

        myAdapter = new MyAdapter();
        mPullRefreshListView.setAdapter(myAdapter);
        mMainHandler = new MyHandler(mContext);
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
        token = LoadActivity.getToken();
        customerId = LoadActivity.getmCustomerId();
        if (token == "" || customerId <0){
            return;
        }
        new Thread() {
            @Override
            public void run() {
                String token = LoadActivity.getToken();
                int customerId = LoadActivity.getmCustomerId();
                Log.d("xie", "token = " + token);
                Log.d("xie", "customerId = " + customerId);
                onRefreshData();
            }
        }.start();
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
            holder.time.setText(JsonUtil.getDate(data.createTime));
            holder.used.setText(data.useFrequency + "");
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
            bundle.putString("purchaseRecordId", mPurchaseResponseData.projectId+"");
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
            mPurchaseRecordResponseJson =
                    JsonUtil.parsoJsonWithGson(result, PurchaseRecordResponseJson.class);
            for(int i=0; i<mPurchaseRecordResponseJson.data.length; i++) {
                mPurchaseContentRecord.add(mPurchaseRecordResponseJson.data[i]);
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
                    Log.d("xie", "health: MyHandler->ADAPTER_DATA_REFRESH");
                    mTextview.setVisibility(View.GONE);
//                    mSwipeLayout.setRefreshing(false);
                    break;
                case ADAPTER_DATA_CHANGE:
                    Log.d("xie", "health: MyHandler->ADAPTER_DATA_CHANGE");
                    mPullRefreshListView.onRefreshComplete();
                    mTextview.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    break;
                case DISPLAY_TEXT:
                    Log.d("xie", "health: MyHandler->DISPLAY_TEXT");
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
        requestBody = new FormBody.Builder()
                .add("token", LoadActivity.getToken())
                .add("customerId", customerId+"")
                .build();
        NetPostUtil.post(Constants.GET_PURCHASE_WITH_ID, requestBody, mHealthManagerCallback);
    }

    /**
     * 设置下拉刷新的listview的动作
     */
    private void initPTRListView() {
        //设置拉动监听器
            mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //设置下拉时显示的日期和时间
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                    // 更新显示的label
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
                    // 开始执行异步任务，传入适配器来进行数据改变
                    onRefreshData();

                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //设置下拉时显示的日期和时间
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                    // 更新显示的label
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
                    // 开始执行异步任务，传入适配器来进行数据改变
                    onRefreshData();
                }
        });

        // 添加滑动到底部的监听器
        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(mContext, "已经到底了", Toast.LENGTH_SHORT).show();
            }
        });

        //mPullRefreshListView.isScrollingWhileRefreshingEnabled();//看刷新时是否允许滑动
        //在刷新时允许继续滑动
        mPullRefreshListView.setScrollingWhileRefreshingEnabled(true);
        //mPullRefreshListView.getMode();//得到模式
        //上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        /**
         * 设置反馈音效
         */
//        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(mContext);
//        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
//        soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
//        soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
//        mPullRefreshListView.setOnPullEventListener(soundListener);
    }
}
