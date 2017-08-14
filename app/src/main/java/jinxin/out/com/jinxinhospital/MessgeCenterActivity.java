package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.MessageCenter.Message;
import jinxin.out.com.jinxinhospital.MessageCenter.MessageResponseJson;
import jinxin.out.com.jinxinhospital.News.News;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessgeCenterActivity extends UserAppCompatActivity {
    private static final int ADAPTER_DATA_CHANGE = 0x11;
    private static final int ADAPTER_DATA_REFRESH = 0x12;
    private static final int DISPLAY_TEXT = 0x13;
    private PullToRefreshListView mPullRefreshListView;
    private Context mContext;
    private MyAdapter myAdapter;
    private MyHandler mMainHandler;
    private TextView mTextview;
    private String mMessage = "";
    private String token;
    private int customerId;

    private List<Message> mMessageList = new ArrayList<>();
    private MessageResponseJson mMessageResponseJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setToolBarTitle(getApplicationContext().getString(R.string.user_message_center_title));
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        mMainHandler = new MyHandler();
        mPullRefreshListView = findViewById(R.id.message_custorm_layout_list);
        mTextview = findViewById(R.id.message_message);
        myAdapter = new MyAdapter();
        mPullRefreshListView.setAdapter(myAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefreshData();
    }

    private void onRefreshData() {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("customerId", customerId +"")
                .add("page", 1 + "")
                .add("size", 10 + "")
                .build();
        NetPostUtil.post(Constants.GET_PUSH_LIST_WITH_ID, requestBody, mMessageListCallback);
    }

    private Callback mMessageListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("xie", "mMessageListCallback onFailure");
            mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            mMessageList.clear();
            Log.d("xie", "mMessageListCallback: result"+result);
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code < 0) {
                mMessage = module.message;
                Log.d("xie", "MessageCenter: mMessage = " + mMessage);
                mMainHandler.sendEmptyMessage(DISPLAY_TEXT);
                return;
            }
            mMessageResponseJson =
                    JsonUtil.parsoJsonWithGson(result, MessageResponseJson.class);
            for(int i=0; i < mMessageResponseJson.data.length-1; i++) {
                Log.d("xie" , "i = " + i);
                mMessageList.add(mMessageResponseJson.data[i]);
            }
            mMainHandler.sendEmptyMessage(ADAPTER_DATA_CHANGE);

        }
    };

    private class MyAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView time;
            public TextView type;
            public TextView title;
            public TextView content;
        }

        @Override
        public int getCount() {
            return mMessageList.size();
        }

        @Override
        public Object getItem(int i) {
            return mMessageList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.message_item, viewGroup, false);
                holder = new ViewHolder();
                holder.title = view.findViewById(R.id.message_title);
                holder.time = view.findViewById(R.id.message_time);
                holder.type = view.findViewById(R.id.message_type);
                holder.content = view.findViewById(R.id.message_content);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Message data = mMessageList.get(i);
            holder.title.setText(data.title);
            holder.time.setText(JsonUtil.getDate(data.createTime));
            String type = "";
            if (data.type == 1) {
                type = "群体推送";
            }
            holder.type.setText( "消息类型： " + data.type);
            holder.content.setText(data.content + "");
            return view;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case ADAPTER_DATA_CHANGE:
                    Log.d("xie", "Message: MyHandler->ADAPTER_DATA_CHANGE");
                    mPullRefreshListView.onRefreshComplete();
                    mTextview.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    break;
                case DISPLAY_TEXT:
                    Log.d("xie", "Message: MyHandler->DISPLAY_TEXT");
                    mTextview.setVisibility(View.VISIBLE);
                    mTextview.setText(mMessage);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_messge_center;
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
