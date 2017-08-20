package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jinxin.out.com.jinxinhospital.BaseFragment;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.MainActivity;
import jinxin.out.com.jinxinhospital.R;
import jinxin.out.com.jinxinhospital.VIP.VipData;
import jinxin.out.com.jinxinhospital.VIP.VipResponseJson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/15.
 */

public class VipFragment extends BaseFragment {
    private int mIndex;
    private ListView mVipListView;
    private LinearLayout mReservationLayout;
    private LinearLayout mPowerLayout;
    private MainActivity mContext;
    private List<VipData> mVipMsgList = new ArrayList<>();
    private MyAdapter mAdapter;
    private String token;
    private int customerId;
    private MyHandler mHandler;
    private TextView mTempText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
        mHandler = new MyHandler(mContext);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        if (token == "" || customerId <0){
            return;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = getArguments().getInt(MainActivity.KEY_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vip_page, container, false);
        mAdapter = new MyAdapter();
        mVipListView= view.findViewById(R.id.vip_list);
        mReservationLayout = view.findViewById(R.id.vip_reservation);
        mPowerLayout = view.findViewById(R.id.vip_power);
        mTempText = view.findViewById(R.id.vip_temp);
        mReservationLayout.setOnClickListener(mOnClickListener);
        mPowerLayout.setOnClickListener(mOnClickListener);
        getData();
        mVipListView.setAdapter(mAdapter);
        return view;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.vip_reservation:
                    startActivity(new Intent("android.intent.action.VIP_RESERVATION_ACTION"));
                    break;
                case R.id.vip_power:
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "vip_power");
                    Intent intent = new Intent("android.intent.action.SHOW_DETAIL_ACTION");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    private void getData(){
        RequestBody requestBody = new FormBody.Builder()
                .add("token",  token)
                .add("customerId", customerId + "")
                .add("page", 1+"")
                .add("size", 20 + "")
                .build();
        NetPostUtil.post(Constants.GET_VIP_MESSAGE_LIST, requestBody, mVipListCallback);
    }

    private Callback mVipListCallback = new Callback() {
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
            VipResponseJson mVipResponseJson =
                    JsonUtil.parsoJsonWithGson(result, VipResponseJson.class);
            mVipMsgList.clear();
            for(int i=0; i<mVipResponseJson.data.length; i++) {
                mVipMsgList.add(mVipResponseJson.data[i]);
            }
            if (mVipMsgList.isEmpty())
            {
                mHandler.sendEmptyMessage(0x22);
            } else {
                mHandler.sendEmptyMessage(0x11);
            }
        }
    };

    private class MyHandler extends Handler {
        public MyHandler(Context context) {
            super(context.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    mTempText.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 0x22:
                    mTempText.setVisibility(View.VISIBLE);
                default:
                    break;
            }
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
            return mVipMsgList.size();
        }

        @Override
        public Object getItem(int i) {
            return mVipMsgList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            view = LayoutInflater.from(mContext).inflate(R.layout.news_item, viewGroup, false);
            holder = new ViewHolder();
            holder.mImageView = view.findViewById(R.id.news_img);
            holder.name = view.findViewById(R.id.news_title);
            holder.summary = view.findViewById(R.id.news_content);
            holder.mItem = view.findViewById(R.id.news_item);
            view.setTag(holder);

            final VipData data = mVipMsgList.get(i);
            holder.mImageView.setImageResource(R.drawable.vipyy);
            holder.name.setText(data.title);
            holder.summary.setText(data.summary);
            holder.mItem.setTag(data.id);
            holder.mItem.setOnClickListener(onClickListener);
            //TODO:
            return view;
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 0;
                try {
                    id = Integer.parseInt(String.valueOf(view.getTag()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                Bundle bundle = new Bundle();
                bundle.putString("type", "vip");
                bundle.putInt("id", id);
                Intent intent = new Intent("android.intent.action.SHOW_DETAIL_ACTION");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPageChange(int newPage) {
    }
}
