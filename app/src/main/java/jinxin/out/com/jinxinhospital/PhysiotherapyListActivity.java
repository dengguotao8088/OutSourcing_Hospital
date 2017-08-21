package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.News.News;
import jinxin.out.com.jinxinhospital.Physiotherapy.Physiotherapy;
import jinxin.out.com.jinxinhospital.Physiotherapy.PhysiotherapyResponseJson;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin on 2017/8/18.
 */

public class PhysiotherapyListActivity extends UserAppCompatActivity{

    private PullToRefreshListView mPullRefreshListView;
    private List<Physiotherapy> mPhysiotherapyList = new ArrayList<>();
    private boolean isPull = false;
    private int page;
    private MyAdapter myAdapter;
    private MyHandler myHandler;
    private int mId;
    private String mTitle;
    private TextView mEmptyMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        myHandler = new MyHandler(this);
        mId = bundle.getInt("id", -1);
        mTitle = bundle.getString("title", "");
        setToolBarTitle(mTitle);
        mPullRefreshListView = findViewById(R.id.physiotherapy_listview);
        mEmptyMsg = findViewById(R.id.phy_empty);
        myAdapter = new MyAdapter();
        mPullRefreshListView.setAdapter(myAdapter);
        initPTRListView();
        onRefreshData();
    }

    private void onRefreshData() {
        page = 1;
        isPull = false;
        RequestBody requestBody = new FormBody.Builder()
                .add("page", page + "")
                .add("size", 9999 + "")
                .add("physiotherapyTypeId", mId+"")
                .build();
        NetPostUtil.post(Constants.GET_PHYSIOTHERPY_LIST, requestBody, mPhysiotherapyListCallback);
    }

    private void onPullData() {
        onRefreshData();
//        isPull = true;
//        RequestBody requestBody = new FormBody.Builder()
//                .add("page", (page++) + "")
//                .add("size", 10 + "")
//                .add("physiotherapyTypeId", mId+"")
//                .build();
//        NetPostUtil.post(Constants.GET_PHYSIOTHERPY_LIST, requestBody, mPhysiotherapyListCallback);
    }


    Callback mPhysiotherapyListCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie" , "result = " + result);
            if (result.contains("502  Bad Gateway")) {
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                return;
            }
            PhysiotherapyResponseJson  mPhysiotherapyResponseJson=
                    JsonUtil.parsoJsonWithGson(result, PhysiotherapyResponseJson.class);
            if (!isPull) {
                mPhysiotherapyList.clear();
            }
            for(int i=0; i<mPhysiotherapyResponseJson.data.length; i++) {
                mPhysiotherapyList.add(mPhysiotherapyResponseJson.data[i]);
            }
            myHandler.sendEmptyMessage(0x11);
        }
    };

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPullRefreshListView.onRefreshComplete();
            if (mPhysiotherapyList.isEmpty()) {
                mEmptyMsg.setVisibility(View.VISIBLE);
            } else {
                mEmptyMsg.setVisibility(View.GONE);
            }
            myAdapter.notifyDataSetChanged();
        }
    }

    private class MyAdapter extends BaseAdapter {

        public class ViewHolder {
            public ImageView mImageView;
            public TextView name;
            public TextView summary;
            public LinearLayout mItem;
        }

        @Override
        public int getCount() {
            return mPhysiotherapyList.size();
        }

        @Override
        public Object getItem(int i) {
            return mPhysiotherapyList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        //设置错误监听
        RequestListener<String,GlideDrawable> errorListener=new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                Log.e("xie",e.toString()+"  model:"+model+" isFirstResource: "+isFirstResource);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.e("xie","isFromMemoryCache:"+isFromMemoryCache+"  model:"+model+" isFirstResource: "+isFirstResource);
                return false;
            }
        };

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(PhysiotherapyListActivity.this).inflate(R.layout.news_item, viewGroup, false);
                holder = new ViewHolder();
                holder.mImageView = view.findViewById(R.id.news_img);
                holder.name = view.findViewById(R.id.news_title);
                holder.summary = view.findViewById(R.id.news_content);
                holder.mItem = view.findViewById(R.id.news_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final Physiotherapy data = mPhysiotherapyList.get(i);
            Glide.with(PhysiotherapyListActivity.this).load(data.coverPath)
                    .override(80,80)
                    .error(R.drawable.load_fail)
                    .placeholder(R.drawable.loading)
                    .listener(errorListener)
                    .into(holder.mImageView);
            holder.name.setText(data.title);
            holder.summary.setText(data.summary);
            holder.mItem.setTag(R.id.tag_first, data.title);
            holder.mItem.setTag(R.id.tag_second, data.id);
            holder.mItem.setOnClickListener(mOnClickListener);
            //TODO:
            return view;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String title = (String) view.getTag(R.id.tag_first);
            int id = (int) view.getTag(R.id.tag_second);
            Bundle bundle = new Bundle();
            bundle.putString("type", "physiotherapy");
            bundle.putInt("id", id);
            Intent intent = new Intent("android.intent.action.SHOW_DETAIL_ACTION");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    /**
     * 设置下拉刷新的listview的动作
     */
    private void initPTRListView() {
        //设置拉动监听器
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(PhysiotherapyListActivity.this, System.currentTimeMillis(),
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
                String label = DateUtils.formatDateTime(PhysiotherapyListActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // 更新显示的label
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
                // 开始执行异步任务，传入适配器来进行数据改变
                onPullData();
            }
        });

        // 添加滑动到底部的监听器
        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(PhysiotherapyListActivity.this, "已经到底了", Toast.LENGTH_SHORT).show();
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

    @Override
    protected int getLayoutId() {
        return R.layout.physiotherapy_list_layout;
    }
}
