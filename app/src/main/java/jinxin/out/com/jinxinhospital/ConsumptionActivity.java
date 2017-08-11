package jinxin.out.com.jinxinhospital;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jinxin.out.com.jinxinhospital.ConsumptionRecord.ConsumptionRecord;
import jinxin.out.com.jinxinhospital.ConsumptionRecord.ConsumptionResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.PurchaseRecord.PurchaseRecordResponseJson;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin on 2017/8/11.
 */

public class ConsumptionActivity extends UserAppCompatActivity{

    private static final int ADAPTER_DATA_CHANGE = 0x11;
    private static final int ADAPTER_DATA_REFRESH = 0x12;
    private static final int DISPLAY_TEXT = 0x13;
    private String mProjectName = "";
    private String mPurchaseRecordId = "";
    private TextView mTitleTextView;
    private TextView mMessageTextView;
    private PullToRefreshListView mPullRefreshListView;
    private Context mContext;
    private MyHandler mMainHandler;
    private MyAdapter myAdapter;
    private List<ConsumptionRecord>  mConsumptionRecordList = new ArrayList<>();
    private ConsumptionResponseJson mConsumptionResponseJson = null;
    private String mMessage;

    private String mRemark;

    @Override
    protected int getLayoutId() {
        return R.layout.consumptionlist_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setToolBarTitle("消费记录");
        Bundle bundle = getIntent().getExtras();
        mProjectName = bundle.getString("projectName");
        mPurchaseRecordId = bundle.getString("purchaseRecordId");
        mRemark = bundle.getString("remark");

        mTitleTextView = findViewById(R.id.consumption_title);
        mMessageTextView = findViewById(R.id.consumption_message);
        mPullRefreshListView = findViewById(R.id.my_consumption_layout_list);
        mTitleTextView.setText(mProjectName);
        myAdapter = new MyAdapter();
        mMainHandler = new MyHandler(mContext);

        mPullRefreshListView.setAdapter(myAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefreshData();
    }

    private class MyAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView empName;
            public TextView status;
            public TextView time;
            public TextView daySymptom;
            public Button comment;
            public Button inVaild;
        }

        @Override
        public int getCount() {
            return mConsumptionRecordList.size();
        }

        @Override
        public Object getItem(int i) {
            return mConsumptionRecordList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.consumption_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.empName = view.findViewById(R.id.consumption_waiter);
                holder.time = view.findViewById(R.id.consumption_time);
                holder.daySymptom = view.findViewById(R.id.consumption_daySymptom);
                holder.status = view.findViewById(R.id.consumption_status);

                holder.comment = view.findViewById(R.id.consumption_comment);
                holder.inVaild = view.findViewById(R.id.consumption_invalid);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            ConsumptionRecord data = mConsumptionRecordList.get(i);
            holder.empName.setText(data.empName);
            holder.time.setText(JsonUtil.getDate(data.createTime));
            holder.daySymptom.setText(data.daySymptom);
            holder.status.setText(data.statusName);
            //TODO:
            holder.comment.setOnClickListener(null);
            holder.comment.setTag(null);

            holder.inVaild.setOnClickListener(null);
            holder.inVaild.setTag(null);
            return view;
        }
    }
    private Callback mConsumptionListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("xie", "mHealthManagerCallback onFailure");
            mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            mConsumptionRecordList.clear();
            Log.d("xie", "mConsumptionListCallback: result"+result);
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code < 0) {
                mMessage = module.message;
                Log.d("xie", "health: mMessage = " + mMessage);
                mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
                return;
            }

            mConsumptionResponseJson =
                    JsonUtil.parsoJsonWithGson(result, ConsumptionResponseJson.class);
            for(int i=0; i<mConsumptionResponseJson.data.datas.length; i++) {
                mConsumptionRecordList.add(mConsumptionResponseJson.data.datas[i]);
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
                    mMessageTextView.setVisibility(View.GONE);
//                    mSwipeLayout.setRefreshing(false);
                    break;
                case ADAPTER_DATA_CHANGE:
                    Log.d("xie", "health: MyHandler->ADAPTER_DATA_CHANGE");
                    mPullRefreshListView.onRefreshComplete();
                    mMessageTextView.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    break;
                case DISPLAY_TEXT:
                    Log.d("xie", "health: MyHandler->DISPLAY_TEXT");
                    mMessageTextView.setVisibility(View.VISIBLE);
                    mMessageTextView.setText(mMessage);
                    break;
                default:
                    break;
            }
        }
    }
    private void onRefreshData() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", LoadActivity.getToken())
                .add("purchaseRecordId", mPurchaseRecordId)
                .add("projectName", mProjectName)
                .add("remark", mRemark)  //xie
                .build();
        NetPostUtil.post(Constants.GET_CONSUMPTIONRECORD_LIST_WITH_ID, requestBody, mConsumptionListCallback);
    }

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

    }
}
