package jinxin.out.com.jinxinhospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jinxin.out.com.jinxinhospital.InformedConsentRecord.InformedConsentRecord;
import jinxin.out.com.jinxinhospital.InformedConsentRecord.InformedConsentResponseJson;
import jinxin.out.com.jinxinhospital.JsonModule.BaseModule;
import jinxin.out.com.jinxinhospital.JsonModule.Constants;
import jinxin.out.com.jinxinhospital.JsonModule.JsonUtil;
import jinxin.out.com.jinxinhospital.JsonModule.NetPostUtil;
import jinxin.out.com.jinxinhospital.R;
import jinxin.out.com.jinxinhospital.view.UserAppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin on 2017/8/14.
 */

public class InformedConsentListActivity extends UserAppCompatActivity {

    private ListView mListView;
    private TextView mTextView;
    private SimpleAdapter mAdapter;
    private Context mContext;
    private List<InformedConsentRecord> mlist= new ArrayList<>();
    private List<Map<String, Object>> mMap = new ArrayList<Map<String, Object>>();
    private Handler mHandler;
    private String token;
    private int customerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setToolBarTitle(getApplicationContext().getString(R.string.user_zhiqing_title));
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("jinxin_clien_app", 0);
        token = sharedPreferences.getString("token", null);
        customerId = sharedPreferences.getInt("customerId", -1);
        mHandler = new Handler(mContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x11:
                        mTextView.setVisibility(View.VISIBLE);
                        break;
                    case 0x22:
                        mTextView.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                }
                super.handleMessage(msg);
            }
        };
        mListView = findViewById(R.id.infored_list);
        mTextView = findViewById(R.id.infored_message);
        mAdapter = new SimpleAdapter(mContext, mMap, R.layout.informed_item, new String[]{"name"}, new int[]{R.id.name});
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", mlist.get(i).id );
                bundle.putString("name", mlist.get(i).informedConsentTemplateName);
                Intent intent = new Intent("android.intent.action.ZHIQIN_DETAIL_CONTENT_ACTION");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("xie", "ID = " +customerId);
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("customerId", customerId + "")
                .build();
        NetPostUtil netPostUtil = new NetPostUtil(this);
        NetPostUtil.post(Constants.GET_CONSENT_LIST_WITH_ID, requestBody, mCallback);
    }

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mTextView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();
            Log.d("xie", "Informed List: result = " + result);
            if (result.contains("502  Bad Gateway")) {
                mHandler.sendEmptyMessage(0x11);
                return;
            }
            BaseModule module = JsonUtil.parsoJsonWithGson(result, BaseModule.class);
            if (module.code != 0 ) {
                mHandler.sendEmptyMessage(0x11);
                return;
            }
            mlist.clear();
            mMap.clear();
            InformedConsentResponseJson mResponseJson
                    = JsonUtil.parsoJsonWithGson(result, InformedConsentResponseJson.class);
            for(int i=0; i<mResponseJson.data.length; i++) {
                mlist.add(mResponseJson.data[i]);
                Map<String, Object> listem = new HashMap<String, Object>();
                listem.put("name", mResponseJson.data[i].informedConsentTemplateName);
                mMap.add(listem);
            }
            mHandler.sendEmptyMessage(0x22);
        }
    };

    @Override
    protected boolean isShowBacking() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.infored_list_layout;
    }
}
