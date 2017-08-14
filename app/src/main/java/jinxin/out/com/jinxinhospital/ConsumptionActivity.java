package jinxin.out.com.jinxinhospital;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.EditText;
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
    private String token;
    private int customerId;

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
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
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
            holder.comment.setOnClickListener(mCommentListener);
            holder.comment.setTag(data);

            holder.inVaild.setOnClickListener(mInVaildListener);
            holder.inVaild.setTag(data);
            return view;
        }
    }

    private View.OnClickListener mCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ConsumptionRecord data = (ConsumptionRecord)view.getTag();
            showConmentDialog(data);
        }
    };
    private View.OnClickListener mInVaildListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ConsumptionRecord data = (ConsumptionRecord)view.getTag();
            showInvaildDialog(data);
        }
    };
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
            mMessage = module.message;
            Log.d("xie", "mConsumptionListCallback: mMessage = " + mMessage);
            if (module.code != 0) {
                mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
                return;
            }
            if (mMessage.equals("")) {
                mMessage = "该项目还没有消费";
                mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
                return;
            }

            try {
                mConsumptionResponseJson =
                        JsonUtil.parsoJsonWithGson(result, ConsumptionResponseJson.class);
                mConsumptionRecordList.clear();
                for(int i=0; i<mConsumptionResponseJson.data.datas.length; i++) {
                    mConsumptionRecordList.add(mConsumptionResponseJson.data.datas[i]);
                }
                mMainHandler.sendEmptyMessage(ADAPTER_DATA_CHANGE);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
            }
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
                    onRefreshData();
                    break;
                case ADAPTER_DATA_CHANGE:
                    Log.d("xie", "health: MyHandler->ADAPTER_DATA_CHANGE");
                    mPullRefreshListView.onRefreshComplete();
                    mTitleTextView.setVisibility(View.VISIBLE);
                    mMessageTextView.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    break;
                case DISPLAY_TEXT:
                    Log.d("xie", "health: MyHandler->DISPLAY_TEXT");
                    mTitleTextView.setVisibility(View.GONE);
                    mMessageTextView.setVisibility(View.VISIBLE);
                    mMessageTextView.setText(mMessage);
                    break;
                case 0x22:
                    Toast.makeText(mContext,mMessage , Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    private void showConmentDialog(final ConsumptionRecord data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("评论：");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View alertLayout = inflater.inflate(R.layout.dialog_edittext, null);
        final EditText dialogText = (EditText)alertLayout.findViewById(R.id.header_dialog_text);
        builder.setView(alertLayout);
        builder.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String dialogMessage = dialogText.getText().toString();
                Log.d("xie", "dialogMessage = " + dialogMessage);
                RequestBody requestBody = new FormBody.Builder()
                        .add("token", token)
                        .add("id", data.id + "")
                        .add("status", data + "")
                        .add("remark", data.remarks)
                        .add("commentLevel", "1")
                        .add("commentContent", dialogMessage)
                        .build();
                NetPostUtil.post(Constants.UPDATE_CONSUMPTIONRECORD, requestBody, mCommentCallback);
                return;
//                SharedPreferences sharedPreferences = mContext.getSharedPreferences("battery_alert_pref",0);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("isSkipMessage2",checkBoxResult);
//                editor.commit();
//                mStandardModePreference.setChecked(true,false);
//                mHighModePreference.setChecked(false);
//                sendStandardSaverBroadcast(true);
            }
        });
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builder.show();
    }

    private Callback mCommentCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("xie", "mCommentsCallback onFailure.");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            } else {
                mMessage = "网络连接异常";
                mMainHandler.sendEmptyMessage(0x22);
            }
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 0) {
                mMessage = "评论成功";
                mMainHandler.sendEmptyMessage(0x22);
            } else {
                mMessage = baseModule.message;
                mMainHandler.sendEmptyMessage(0x22);
            }
        }
    };

    private Callback mInvaildCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("xie", "mInvaildCallback onFailure.");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            } else {
                mMessage = "网络连接异常";
                mMainHandler.sendEmptyMessage(0x22);
            }
            BaseModule baseModule = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (baseModule.code == 0) {
                mMessage = "实施作废申请成功";
                mMainHandler.sendEmptyMessage(0x22);
            } else {
                mMessage = baseModule.message;
                mMainHandler.sendEmptyMessage(0x22);
            }
        }
    };

    private void showInvaildDialog(final ConsumptionRecord data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("申请作废理由：");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View alertLayout = inflater.inflate(R.layout.dialog_edittext, null);
        final EditText dialogText = (EditText)alertLayout.findViewById(R.id.header_dialog_text);
        builder.setView(alertLayout);
        builder.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String dialogMessage = dialogText.getText().toString();
                //: change to invaild request
                Log.d("xie", "dialogMessage = " + dialogMessage);
                Log.d("xie", Constants.REQUEST_IMPEMENTATIONVOID+"token="+token+"&consumption_record_id="+data.id + ""+"&content="+dialogMessage);
                RequestBody requestBody = new FormBody.Builder()
                        .add("token", token)
                        .add("consumption_record_id", data.id + "")
                        .add("content", dialogMessage)
                        .build();
                NetPostUtil.post(Constants.REQUEST_IMPEMENTATIONVOID, requestBody, mInvaildCallback);
                return;
            }
        });
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builder.show();
    }

    private void onRefreshData() {
        RequestBody requestBody = new FormBody.Builder()
                    .add("token", token)
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
